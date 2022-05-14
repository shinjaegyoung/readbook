package com.example.readbook

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readbook.databinding.ActivityProductRegBinding
import com.example.readbook.databinding.ItemMarketRegBinding
import com.example.readbook.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

private var auth = Firebase.auth

class ProductRegActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProductRegBinding
    private val product = Product()
    private val productImg : MutableList<Uri>? = product.pImg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fireDatabase = FirebaseDatabase.getInstance()
        database = Firebase.database.reference

        val newRef = fireDatabase.getReference("products").push()
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        val user = auth.currentUser

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        //이미지 등록
        var imageUri: Uri? = null
        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    imageUri = result.data?.data //이미지 경로 원본
                    product.pImg?.add(imageUri!!)
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
            product.pName = binding.edPName.text.toString()
            product.pPrice = binding.edPrice.text.toString().toInt()
            product.pDes = binding.edDes.text.toString()
            product.pViewCount = 0
            product.status = "판매중"
            product.user = user?.displayName.toString()
            product.regDate = curTime

            if (binding.edPName.text.isEmpty() && binding.edPrice.text.isEmpty() && binding.edDes.text.isEmpty()) {
                Toast.makeText(this, "상품 정보를 빠짐 없이 입력해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                FirebaseStorage.getInstance()
                    .reference.child("productImages").child("${product.pid}/photo").putFile(imageUri!!)
                    .addOnSuccessListener {
                        var imageUri: Uri?
                        // storage에 이미지 저장
                        FirebaseStorage.getInstance().reference.child("productImages")
                            .child("${product.pid}/photo")
                            .downloadUrl.addOnSuccessListener {
                                imageUri = it
                                Log.d("이미지 URL", "$imageUri, $product")
                                // productlist 에 데이터 저장
                                database.child("productlist").child(product.pid.toString())
                                    .setValue(product)
                        }
                }
                Toast.makeText(this, "상품이 등록되었습니다.", Toast.LENGTH_SHORT)
                    .show()

//                val intent = Intent(this, MarketFragment::class.java)
//                startActivity(intent)
            }
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewPR.layoutManager=layoutManager
        binding.recyclerViewPR.adapter= RecyclerViewAdapter(productImg)
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }
    private fun reload() {
    }

    // 상품 이미지 recyclerViewAdapter
    inner class RecyclerViewAdapter(private val productImg:MutableList<Uri>?) : RecyclerView.Adapter<RecyclerViewAdapter.ProductImgViewHolder>() {

        inner class ProductImgViewHolder(val binding:ItemMarketRegBinding):RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImgViewHolder =
            ProductImgViewHolder(ItemMarketRegBinding.inflate(LayoutInflater.from(parent.context),parent,false))

        override fun onBindViewHolder(holder: ProductImgViewHolder, position: Int) {
            val binding=(holder).binding
            if(productImg != null){
                binding.itemPImg.setImageURI(productImg[position])
                binding.itemPImg.setOnClickListener {
                    productImg?.remove(productImg[position])
                }
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            Log.d("getItemCount", "${productImg?.size}")
            var size : Int
            if(productImg != null){
                size = productImg.size
            }else {
                size = 0
            }
            return size
        }
    }
}



