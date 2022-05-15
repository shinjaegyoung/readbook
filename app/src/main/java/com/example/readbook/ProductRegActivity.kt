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
import com.bumptech.glide.Glide
import com.example.readbook.databinding.ActivityProductRegBinding
import com.example.readbook.databinding.ItemMarketRegBinding
import com.example.readbook.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_readlist.view.*
import kotlinx.android.synthetic.main.activity_splash.view.*
import java.text.SimpleDateFormat
import java.util.*

private var auth = Firebase.auth

class ProductRegActivity : AppCompatActivity() {
    private lateinit var binding:ActivityProductRegBinding
    private val product = Product()
    private val productImg = Product.ProductImg()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fireDatabase = FirebaseDatabase.getInstance()
        database = Firebase.database.reference

        val newRef = fireDatabase.getReference("productlist").push()
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        val user = auth.currentUser

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        product.pName = ""
        product.pPrice = ""
        product.pDes = ""
        product.regDate = ""
        product.pid = newRef.key.toString()
        product.pViewCount = 0
        product.status = "판매중"
        product.user = user?.email.toString()
        database.child("productlist").child(product.pid.toString()).setValue(product)

        //이미지 등록
        var imageUri: Uri? = null
        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    imageUri = result.data?.data //이미지 경로 원본
                    productImg.pImg = imageUri.toString()
                    //productImg 에 이미지 저장
                    database.child("productlist").child(product.pid.toString()).child("productImg").push().setValue(productImg)
                    Log.d("이미지", "pImg 성공")
                } else {
                    Log.d("이미지", "pImg 실패")
                }
            }

        // 상품 이미지 추가 버튼 클릭
        binding.btnRegPImg.setOnClickListener {
            val intentImage =
                Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)

        }

        //작성완료 버튼 클릭
        binding.productRegBtn.setOnClickListener {
            product.pName = binding.edPName.text.toString()
            //product.pPrice = binding.edPrice.text.toString().toInt()
            product.pPrice = binding.edPrice.text.toString()
            product.pDes = binding.edDes.text.toString()
            product.regDate = curTime

            if (binding.edPName.text.isEmpty() || binding.edPrice.text.isEmpty() || binding.edDes.text.isEmpty()  ) {
                Toast.makeText(this, "상품 정보를 빠짐 없이 입력해주세요.", Toast.LENGTH_SHORT)
                    .show()

            } else {
                // storage 에 이미지 저장
                FirebaseStorage.getInstance()
                    .reference.child("productImages").child("${product.pid}/photo").putFile(imageUri!!)
                    .addOnSuccessListener {
                        var imageUri: Uri?
                        FirebaseStorage.getInstance().reference.child("productImages")
                            .child("${product.pid}/photo")
                            .downloadUrl.addOnSuccessListener {
                                imageUri = it
                                Log.d("이미지 URL", "$imageUri, $product")
                                // productlist 에 데이터 수정
                                updateDatas(product.pName!!, product.pPrice!!, product.pDes!!, product.regDate)
                        }
                }
                Toast.makeText(this, "상품이 등록되었습니다.", Toast.LENGTH_SHORT)
                    .show()

                // 목록으로 이동
//                val intent = Intent(this, MarketFragment::class.java)
//                startActivity(intent)
            }
        }

        val layoutManager = LinearLayoutManager(this@ProductRegActivity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewPR.layoutManager=layoutManager
        binding.recyclerViewPR.adapter= RecyclerViewAdapter()
    }

    private fun updateDatas(pName: String, pPrice: String, pDes: String, regDate: Any) {
        database = FirebaseDatabase.getInstance().getReference("productlist").child(product.pid.toString())
        val product = mapOf<String, Any?>(
            "pname" to pName,
            "pprice" to pPrice,
            "pdes" to pDes,
            "regDate" to regDate
        )
        database.updateChildren(product)
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
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ProductImgViewHolder>() {

        private val productImgs = ArrayList<Product.ProductImg>()

        init{
            getPImgList()
        }

        fun getPImgList(){
            database.child("productlist").child(product.pid.toString()).child("productImg").addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    productImgs.clear()
                    for(data in snapshot.children){
                        val item = data.getValue<Product.ProductImg>()
                        productImgs.add(item!!)
                        println(productImgs)
                    }
                    notifyDataSetChanged()
                }
            })
        }

        inner class ProductImgViewHolder(val binding:ItemMarketRegBinding):RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImgViewHolder =
            ProductImgViewHolder(ItemMarketRegBinding.inflate(LayoutInflater.from(parent.context),parent,false))

        override fun onBindViewHolder(holder: ProductImgViewHolder, position: Int) {
            val binding=(holder).binding
            Glide.with(holder.itemView.context)
                .load(productImgs[position]?.pImg)
                .into(holder.binding.itemPImg)
            if(productImgs != null){
                binding.itemPImg.setOnClickListener {
                    productImgs?.remove(productImgs[position])
                }
            }
        }

        override fun getItemCount(): Int {
            Log.d("getItemCount", "${productImgs?.size}")
            var size : Int
            if(productImg != null){
                size = productImgs.size
            }else {
                size = 0
            }
            return size
        }
    }
}



