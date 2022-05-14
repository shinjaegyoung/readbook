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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readbook.databinding.ActivityProductRegBinding
import com.example.readbook.databinding.ItemMarketRegBinding
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
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_product_reg.*
import kotlinx.android.synthetic.main.activity_registration.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private var auth = Firebase.auth
private var fireDatabase = FirebaseDatabase.getInstance().reference
private var productuid : String? = null

class ProductRegActivity : AppCompatActivity() {
    lateinit var binding:ActivityProductRegBinding
    val product = Product()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var fireDatabase = FirebaseDatabase.getInstance()
        database = Firebase.database.reference
        var reference : StorageReference = FirebaseStorage.getInstance().getReference()

        val newRef = fireDatabase.getReference("products").push()
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        val user = auth.currentUser
        val userId = user?.uid
        val userIdSt = userId.toString()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        var imageUri: Uri? = null

        //이미지 등록
        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    imageUri = result.data?.data //이미지 경로 원본
                    product.pImg?.add(imageUri.toString())
                    ItemMarketRegBinding.inflate(layoutInflater).itemPImg.setImageURI(imageUri) //이미지 뷰를 바꿈
                    Log.d("이미지", "pImg 성공")
                } else {
                    Log.d("이미지", "pImg 실패")
                }
            }

        binding.btnRegPImg.setOnClickListener {
            val intentImage =
                Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }

        binding.productRegBtn.setOnClickListener {
            product.pid = newRef.key.toString()
            product.pName = binding.edPName.toString()
            product.pPrice = binding.edPrice.toString().toInt()
            product.pDes = binding.edDes.toString()
            product.pImg
            product.pViewCount = 0
            product.status = "판매중"
            product.user = user?.displayName.toString()
            product.regDate = curTime

            if (binding.edPName.text.isEmpty() && binding.edPrice.text.isEmpty() && binding.edDes.text.isEmpty()) {
                Toast.makeText(this, "상품 정보를 빠짐 없이 입력해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                FirebaseStorage.getInstance()
                    .reference.child("productImages").child("${product.pImg}/photo").putFile(imageUri!!)
                    .addOnSuccessListener {
                        var imageUri: Uri? = null
                        FirebaseStorage.getInstance().reference.child("productImages")
                            .child("$userIdSt/photo")
                            .downloadUrl.addOnSuccessListener {
                                imageUri = it
                                Log.d("이미지 URL", "$imageUri")
                                database.child("productlist").child(product.pid.toString())
                                    .setValue(product)
                        }
                }
                Toast.makeText(this, "상품이 등록되었습니다.", Toast.LENGTH_SHORT)
                    .show()

                val intent = Intent(this, MarketFragment::class.java)
                startActivity(intent)
            }
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewPR.layoutManager=layoutManager
        binding.recyclerViewPR.adapter=RecyclerViewAdapter(product.pImg)
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload();
        }
    }
    private fun reload() {
    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    // 상품 이미지 recyclerViewAdapter
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.productImgViewHolder>() {

        private val product = ArrayList<Product>()
        private var user : User? = null

        init{
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
                    product.pImg?.clear()
                    for(data in snapshot.children){
                        val item = data.getValue<ChatModel.Comment>()
                        product.pImg?.add(item!!)
                        println(product.pImg?.toString())
                    }
                    notifyDataSetChanged()
                }
            })
        }

        inner class productImgViewHolder(val binding:ItemMarketRegBinding):RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): productImgViewHolder =
            productImgViewHolder(ItemMarketRegBinding.inflate(LayoutInflater.from(parent.context),parent,false))

        override fun onBindViewHolder(holder: productImgViewHolder, position: Int) {
            val binding=(holder as productImgViewHolder).binding
            binding.itemPImg.setImageURI(product.pImg[position])
            binding.itemPImg.setOnClickListener{
                product.pImg[position]=null
            }
        }

        override fun getItemCount(): Int {
            return 4
        }
    }
}



