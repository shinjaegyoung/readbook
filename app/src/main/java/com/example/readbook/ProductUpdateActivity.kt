package com.example.readbook

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readbook.databinding.ActivityProductUpdateBinding
import com.example.readbook.databinding.ItemMarketRegBinding
import com.example.readbook.model.Product
import com.example.readbook.model.ProductImg
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

private var auth = Firebase.auth

class ProductUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductUpdateBinding
    private var product = Product()
    private lateinit var productImg : ProductImg
    private var productImgs: ArrayList<ProductImg>? = null
    private val fireDatabase = FirebaseDatabase.getInstance()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_reg_menu, menu)
        if(auth.currentUser?.uid.toString() == intent.getStringExtra("user")){
            return super.onCreateOptionsMenu(menu)
        }else{
            return false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.toolbar_reg_button -> {
            val time = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val curTime = dateFormat.format(Date(time)).toString()

            product.pName = binding.edPName.text.toString()
            product.pPrice = binding.edPrice.text.toString()
            product.pPrice = binding.edPrice.text.toString()
            product.pDes = binding.edDes.text.toString()
            product.regDate = curTime

            if (binding.edPName.text.isEmpty() || binding.edPrice.text.isEmpty() || binding.edDes.text.isEmpty() || productImgs!! == null ) {
                Toast.makeText(this, "?????? ????????? ?????? ?????? ??????????????????.", Toast.LENGTH_SHORT)
                    .show()

            } else {
                // productlist ??? ????????? ??????
                updateDatas(product.pName!!, product.pPrice!!, product.pDes!!, product.regDate)

                Toast.makeText(this, "??????????????? ?????????????????????.", Toast.LENGTH_SHORT)
                    .show()

                // ???????????? ??????
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
            true
        }

        android.R.id.home -> {
            // ???????????? ??????
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // intent??? ??? ????????? ??? NullPointerException ?????? -> ????????????
        try {
            product = Product(
                "${intent.getStringExtra("pid")}",
                "${intent.getStringExtra("pName")}",
                "${intent.getStringExtra("pPrice")}",
                "${intent.getStringExtra("pDes")}",
                "${intent.getStringExtra("user")}",
                intent.getIntExtra("pViewCount", 0),
                "${intent.getStringExtra("regdate")!!}",
                "${intent.getStringExtra("status")}"
            )
        }catch(e:Exception){
            Log.d("error", "nullPointException")
        }

        // ????????? ????????? Uri??? ?????? ArrayList ??????
        productImgs = ArrayList<ProductImg>()
        FirebaseDatabase.getInstance().reference.child("productImg").child("${intent.getStringExtra("pid")}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("lyk", "fail..............")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    productImgs!!.clear()
                    for (data in snapshot.children) {
                        Log.d("?????????data", "${data.getValue<ProductImg>()}")
                        productImgs!!.add(data.getValue<ProductImg>()!!)
                        Log.d("????????? ??????", "${productImgs}")
                        println(data)
                    }

                    //RecyclerViewAdapter
                    val layoutManager = LinearLayoutManager(this@ProductUpdateActivity)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    binding.recyclerViewPR.layoutManager=layoutManager
                    binding.recyclerViewPR.adapter= RecyclerViewAdapter()
                }
            })

        database = Firebase.database.reference

        // ????????? ??? ????????????..
        binding.edPName.setText(intent.getStringExtra("pName"))
        binding.edPrice.setText(intent.getStringExtra("pPrice"))
        binding.edDes.setText(intent.getStringExtra("pDes"))

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

        // ???????????????????????? ????????????(setValue())
        //database.child("productlist").child(product.pid.toString()).setValue(product)

        //????????? ????????? Uri
        var imageUri: Uri? = null

        val getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    imageUri = result.data?.data //????????? ?????? ??????
                    Log.d("????????? url", "${imageUri}")
                    if(imageUri != null){
                        //ArrayList??? ????????? Uri ??????
                        //productImgs?.add(productImg)
                        //Log.d("????????? ?????????", "${productImgs}")
                        // storage ??? ????????? ??????
                        FirebaseStorage.getInstance()
                            .reference.child("productImages").child("${product.pid}/${productImgs?.size!!}").putFile(imageUri!!)
                            .addOnSuccessListener {
                                FirebaseStorage.getInstance().reference.child("productImages")
                                    .child("${product.pid}/${productImgs?.size!!}")
                                    .downloadUrl.addOnSuccessListener {
                                        var productimageUri = it
                                        Log.d("????????? URL", "$productimageUri, $product")
                                        productImg=ProductImg(productimageUri.toString())
                                        productImgs?.add(productImg)
                                        //db??? productImg ??? ????????? ??????
                                        database.child("productImg").child("${product.pid}/${productImgs?.size!!-1}").setValue(productImg)
                                        Log.d("????????? ?????????", "${product.pid}/${productImgs?.size!!-1}")
                                    }
                            }
                    }else{
                        //????????? ????????? ?????? ????????? ??? ????????? ?????????(?????? ??????)
                        ItemMarketRegBinding.inflate(layoutInflater).itemPImg.setImageResource(R.drawable.default_img)
                    }
                    Log.d("?????????", "pImg ??????")
                } else {
                    Log.d("?????????", "pImg ??????")
                }
            }

        // ?????? ????????? ?????? ?????? ??????
        binding.btnRegPImg.setOnClickListener {
            val intentImage =
                Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            //getContent ??????
            getContent.launch(intentImage)

        }
    }

    //????????? ????????? ????????????
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

    // ?????? ????????? recyclerViewAdapter
    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ProductImgViewHolder>() {

        inner class ProductImgViewHolder(val binding: ItemMarketRegBinding): RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImgViewHolder =
            ProductImgViewHolder(ItemMarketRegBinding.inflate(LayoutInflater.from(parent.context),parent,false))

        override fun onBindViewHolder(holder: ProductImgViewHolder, position: Int) {
            // ????????? ????????? ??????????????? ??????
            Glide.with(holder.itemView.context)
                .load(productImgs!![position]?.pImg)
                .into(holder.binding.itemPImg)
            if(productImgs != null){
                holder.binding.itemPImg.setOnClickListener {
                    Log.d("????????? ??????", "????????? ??????")
                    productImgs?.remove(productImgs!![position])
                    Log.d("????????? ??????", "${productImgs!![position]}")
                    FirebaseStorage.getInstance().getReference("productImages").child("${product.pid.toString()}/${position}").delete()
                        .addOnCompleteListener {
                            Log.d("????????? ??????", "?????????")
                            //?????? ??????
                        }
                }
            }
        }

        override fun getItemCount(): Int {
            Log.d("getItemCount", "${productImgs?.size}")
            return productImgs?.size?:0
        }
    }
}