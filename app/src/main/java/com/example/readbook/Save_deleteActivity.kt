package com.example.readbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.readbook.databinding.ActivitySaveDeleteBinding
import com.example.readbook.model.BookNote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth
class Save_deleteActivity : AppCompatActivity() {
    private val BookNote = BookNote()
    val useremail =  Firebase.auth.currentUser?.email.toString()
    var useremail_plus = useremail.replace(".", "+")
    val user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivitySaveDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        val useremail =  Firebase.auth.currentUser?.email.toString()
        val booktitle = findViewById<EditText>(R.id.booktitle).text
        val bookcontent = findViewById<EditText>(R.id.bookcontent).text
        val user = Firebase.auth.currentUser
        val fireDatabase = FirebaseDatabase.getInstance()


        //currentUser = 로그인한 사용자
        val userId = user?.uid
        val userIdSt = userId.toString()
        var useremailarr = useremail.split(".")
        var useremail1 = ""

        for(i in 0 .. useremailarr.size-1 step(1)){
            useremail1 += useremailarr.get(i)
        }



        var useremail_plus = useremail.replace(".", "+")
        val new2Ref = fireDatabase.getReference("bookdiray").child(useremail_plus).push()
        BookNote.bookid = new2Ref.key.toString()

        binding.btnsave.setOnClickListener {


            val booknote = BookNote(
                binding.booktitle.text.toString(),
                binding.bookcontent.text.toString(),
                userId,
                new2Ref.key.toString()

            )

            //database.child("bookdiary").push()
            // .setValue(booknote)
            //database.get().=setValue(booknote)
            //database.child("bookdiary").child("${useremail_plus}").child(BookNote.bookid.toString()).setValue(booknote)
            FirebaseDatabase.getInstance().getReference("bookdiary")
                .child("${useremail_plus}").child(BookNote.bookid.toString()).setValue(booknote)
            /*   intent = Intent(this@ListDetailActivity, ReadListActivity::class.java)
               startActivity(intent)
               finish()
   */
            val intent = Intent(this@Save_deleteActivity , ReadListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}