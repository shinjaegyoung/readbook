package com.example.readbook

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readbook.databinding.ActivityProductRegBinding
import com.example.readbook.fragment.MarketFragment
import com.example.readbook.model.ChatModel
import com.example.readbook.model.Product
import com.example.readbook.model.ProductList
import com.example.readbook.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_product_reg.*
import kotlinx.android.synthetic.main.activity_registration.*

private val fireDatabase = FirebaseDatabase.getInstance().reference
private var productuid : String? = null
private var destinationUid : String? = null
private var uid : String? = null
private var recyclerView : RecyclerView? = null

class ProductRegActivity : AppCompatActivity() {
    lateinit var binding:ActivityProductRegBinding
    val pImg = findViewById<ImageView>(R.id.pImg)

    private var imageUri: Uri? = null //이미지 등록
    // 이미지 리스트로 저장해야 함
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data //이미지 경로 원본
            pImg.setImageURI(imageUri) //이미지 뷰를 바꿈
            Log.d("이미지", "pImg 성공")
        } else {
            Log.d("이미지", "pImg 실패")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProductRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false) // 기존 title 지우기
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 만들기
        getSupportActionBar()?.setHomeAsUpIndicator(R.drawable.ic_back) //뒤로가기 버튼 이미지 지정

        auth = Firebase.auth
        val fireDatabase = FirebaseDatabase.getInstance().reference
        database = Firebase.database.reference

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        var pImgCheck = false

        binding.btnRegPImg.setOnClickListener {
            val intentImage =
                Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
            pImgCheck = true
        }
        val intent = Intent(this, MarketFragment::class.java)
        // 툴바 작성완료 버튼 클릭
        val menu : Menu?
        val inflater = menuInflater
        inflater.inflate(R.menu.product_reg_menu, menu)
        val productReg = menu?.findItem(R.id.toolbar_reg_button)

        productReg.setOnClickListener {
            if (binding.edPName.text.isEmpty() && binding.edPrice.text.isEmpty() && binding.edDes.text.isEmpty()) {
                Toast.makeText(this, "상품 정보를 빠짐 없이 입력해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                database =Firebase.database.reference
                database.child("productList").push().child(pId)

                auth.createUserWithEmailAndPassword(binding.edPName.toString(), binding.edPrice.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = Firebase.auth.currentUser
                            val userId = user?.uid
                            val userIdSt = userId.toString()
                            FirebaseStorage.getInstance()
                                .reference.child(
                                    "userImages"
                                ).child("$userIdSt/photo").putFile(imageUri!!)
                                .addOnSuccessListener {
                                    var userProfile: Uri? = null
                                    FirebaseStorage.getInstance().reference.child(
                                        "userImages"
                                    )
                                        .child("$userIdSt/photo").downloadUrl.addOnSuccessListener {
                                            userProfile = it
                                            Log.d("이미지 URL", "$userProfile")
                                            val productList = ProductList(
                                                user,
                                                Product products = new Product(
                                                    null,
                                                    "binding.edPName.toString()",
                                                    (Int)binding.edPrice.toString(),
                                                    "binding.edDes.toString()",
                                                    userIdSt,
                                                    0,
                                                    0,
                                                    now(),
                                                    pImg,
                                                    "판매중"
                                                )
                                            )
                                            database.child("users").child(userId.toString())
                                                .setValue(user)
                                        }
                                }
                            Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    // 상품 이미지 recyclerViewAdapter
    // 이미지 등록은 버튼 클릭시
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        private val products = ArrayList<Product>()
        private var user : User? = null

        init{
            // 등록한 사진 이미지 리스트 불러오기 product > List<pImg>
            fireDatabase.child("productList").child("product").child(productuid.toString()).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue<User>()
                    messageActivity_textView_topName.text = user?.name
                    getPImgList()
                }
            })
        }

        fun getPImgList(){
            fireDatabase.child("productList").child(productuid.toString()).child("comments").addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    for(data in snapshot.children){
                        val item = data.getValue<ChatModel.Comment>()
                        comments.add(item!!)
                        println(comments)
                    }
                    notifyDataSetChanged()
                    //메세지를 보낼 시 화면을 맨 밑으로 내림
                    recyclerView?.scrollToPosition(comments.size - 1)
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

            return MessageViewHolder(view)
        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.textView_message.textSize = 20F
            holder.textView_message.text = comments[position].message
            holder.textView_time.text = comments[position].time
            if(comments[position].uid.equals(uid)){ // 본인 채팅
                holder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                holder.textView_name.visibility = View.INVISIBLE
                holder.layout_destination.visibility = View.INVISIBLE
                holder.layout_main.gravity = Gravity.RIGHT
            }else{ // 상대방 채팅
                Glide.with(holder.itemView.context)
                    .load(user?.profileImageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.imageView_profile)
                holder.textView_name.text = user?.name
                holder.layout_destination.visibility = View.VISIBLE
                holder.textView_name.visibility = View.VISIBLE
                holder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                holder.layout_main.gravity = Gravity.LEFT
            }
        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView_message: TextView = view.findViewById(R.id.messageItem_textView_message)
            val textView_name: TextView = view.findViewById(R.id.messageItem_textview_name)
            val imageView_profile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
            val layout_destination: LinearLayout = view.findViewById(R.id.messageItem_layout_destination)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val textView_time : TextView = view.findViewById(R.id.messageItem_textView_time)
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }
}