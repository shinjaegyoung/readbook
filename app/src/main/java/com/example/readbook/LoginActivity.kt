package com.example.readbook

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

private lateinit var auth : FirebaseAuth
class LoginActivity : AppCompatActivity() {
    private lateinit var googleSignInClient : GoogleSignInClient
    private val RC_SIGN_IN = 99
    private lateinit var firebaseAuth : FirebaseAuth
    var callbackManager : CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth
        btn_googleSignIn.setOnClickListener {googlesignIn()}
        xml_btn_main_facebookLogin.setOnClickListener { facebookLogin() }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            //'R.string.default_web_client_id' 에는 본인의 클라이언트 아이디를 넣어주시면 됩니다.
            //저는 스트링을 따로 빼서 저렇게 사용했지만 스트링을 통째로 넣으셔도 됩니다.
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        callbackManager = CallbackManager.Factory.create()

        val email = findViewById<EditText>(R.id.et_login_id)
        val password = findViewById<EditText>(R.id.et_login_password)
        val btn_login = findViewById<Button>(R.id.profile_button)

        btn_login.setOnClickListener {
            if(email.text.isEmpty() && password.text.isEmpty()){
                Toast.makeText(this, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                Log.d("Email", "$email, $password")
                email.setText("")
                password.setText("")
            }
            else{
                signIn(email.text.toString(), password.text.toString())
            }
        }

        //회원가입창 Intext
        val btn_registration = findViewById<Button>(R.id.btn_registration)
        btn_registration.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
    private fun signIn(email: String, password : String){
        // [start sign_in_with_email
        val intentMain = Intent(this, MainActivity::class.java)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                Log.d("로그인", "성공")
                val user = auth.currentUser
                updateUI(user)
                finish()
                startActivity(intentMain)
            }else{
            Toast.makeText(this, "정확한 아이디와 비밀번호 입력해주세요", Toast.LENGTH_SHORT).show()
                    Log.d("로그인", "실패")
            updateUI(null)
        }
    }
    // [end sign_in_with_email
}

private fun updateUI(user: FirebaseUser?){

}

public override fun onStart(){
    super.onStart()
    val currentUser = auth.currentUser
    if(currentUser != null){
        reload();


        /*val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account!==null){
            toMainActivity(firebaseAuth.currentUser)
        }*/
    }
}
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("LoginActivity", "Google sign in failed", e)
            }
        }

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    } // onActivityResult End

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.id!!)

        //Google SignInAccount 객체에서 ID 토큰을 가져와서 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 성공", task.exception)
                    toMainActivity(firebaseAuth?.currentUser)
                } else {
                    Log.w("LoginActivity", "firebaseAuthWithGoogle 실패", task.exception)

                }
            }
    }

    fun toMainActivity(user: FirebaseUser?) {
        if(user !=null) { // MainActivity 로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun googlesignIn(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /*private fun signOut() { // 로그아웃
        // Firebase sign out
        firebaseAuth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
        }
    }*/

     fun facebookLogin(){
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile","email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult) {
                    // 로그인 성공시 파이어베이스로 로그인 데이터 넘겨줌
                    handleFacebookAccessTokens(result?.accessToken)

                     val intent =Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }



                override fun onCancel() {
                    reload()
                }

                override fun onError(error: FacebookException) {
                    TODO("Not yet implemented")
                }

            })
    }

     fun handleFacebookAccessTokens(token: AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener{
                task->
                if(task.isSuccessful){
                    // 아이디 , 비밀번호가 맞을때
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(this, task.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
    }
private fun reload() {

    }
}



