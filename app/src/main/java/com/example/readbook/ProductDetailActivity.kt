package com.example.readbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readbook.databinding.ActivityProductDetailBinding
import com.example.readbook.databinding.ItemMarketDetailBinding
import com.example.readbook.model.Product
import com.example.readbook.model.ProductImg
import com.example.readbook.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList

private val fireDatabase = FirebaseDatabase.getInstance().reference
private lateinit var productImgs : ArrayList<ProductImg>
private var auth = Firebase.auth

class ProductDetailActivity : AppCompatActivity() {
    private var product = Product()

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_detail_menu, menu)
        if(auth.currentUser?.uid.toString() == intent.getStringExtra("user")){
            return super.onCreateOptionsMenu(menu)
        }else{
            return false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.updateProduct -> {
            val intent = Intent(this, ProductUpdateActivity::class.java)
            intent.putExtra("pDes", product.pDes)
            intent.putExtra("pName", product.pName)
            intent.putExtra("pPrice", product.pPrice)
            intent.putExtra("pViewCount", product.pViewCount)
            intent.putExtra("pid", product.pid)
            intent.putExtra("status", product.status)
            intent.putExtra("user", product.user)
            intent.putExtra("regdate", product.regDate.toString())
            Log.d("???????????? ?????? ??????","${product}")
            startActivity(intent)
            finish()
            true
        }
        R.id.deleteProduct -> {
            FirebaseDatabase.getInstance().getReference("productlist").child(product.pid.toString()).removeValue()
            FirebaseDatabase.getInstance().getReference("productImg").child(product.pid.toString()).removeValue()
            FirebaseStorage.getInstance().getReference("productImages").child(product.pid.toString()).delete()
            Toast.makeText(this,"????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        android.R.id.home -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        product=Product(
            "${intent.getStringExtra("pid")}",
            "${intent.getStringExtra("pName")}",
            "${intent.getStringExtra("pPrice")}",
            "${intent.getStringExtra("pDes")}",
            "${intent.getStringExtra("user")}",
            intent.getIntExtra("pViewCount", 0),
            "${intent.getStringExtra("regdate")!!}",
            "${intent.getStringExtra("status")}"
        )
        Log.d("product ?????????", "${product}")

        // db??? ????????? ????????? uri ????????????
        productImgs = ArrayList<ProductImg>()
        fireDatabase.child("productImg").child("${intent.getStringExtra("pid")}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("lyk", "fail..............")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    productImgs!!.clear()
                    Log.d("lyk","${fireDatabase.child("productImg").child("${intent.getStringExtra("pid")}")}")
                    Log.d("lyk", "success..............")
                    Log.d("lyk", "${snapshot.value}")
                    for (data in snapshot.children) {
                        Log.d("?????????data", "${data.getValue<ProductImg>()}")
                        productImgs!!.add(data.getValue<ProductImg>()!!)
                        Log.d("????????? ??????", "${productImgs}")
                        println(data)
                    }
                    //RecyclerViewAdapter
                    // ???????????? ????????? ?????? ??????????????? ?????? ??????
                    val layoutManager = LinearLayoutManager(this@ProductDetailActivity)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    binding.recyclerViewPD.layoutManager=layoutManager
                    binding.recyclerViewPD.adapter= RecyclerViewAdapter()


                    Log.d("????????? ????????? ????????????", "${FirebaseStorage.getInstance().getReference("productImg").child("${intent.getStringExtra("pid")}").downloadUrl}")
                }
            })

        // ????????? ????????? ????????????
        Log.d("lyk","${intent.getStringExtra("user")}")
        fireDatabase.child("users").child("${intent.getStringExtra("user")}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue<User>()
                    Log.d("lyk","${user}")
                    Log.d("lyk","${user?.profileImageUrl}")
                    Glide.with(binding.profilePD.context)
                        .load(user?.profileImageUrl)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.profilePD)
                    binding.usernamePD.text = user?.name
                }
            })

        binding.tvProductName.text=intent.getStringExtra("pName")
        binding.tvProductPrice.text=intent.getStringExtra("pPrice")
        binding.tvProductDes.text=intent.getStringExtra("pDes")
        binding.usernamePD.text=intent.getStringExtra("user")
        binding.tvStatus.text=intent.getStringExtra("status")

        // 1:1 ?????? ??????
        binding.btnDetailMarket.setOnClickListener {
            //????????? ?????? ??? ??????
            var destinationUsers = intent.getStringExtra("user")
            val intent = Intent(this, MessageActivity::class.java)
            intent.putExtra("destinationUid", destinationUsers)
            startActivity(intent)
        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<ProductDetailActivity.RecyclerViewAdapter.ProductImgViewHolder>() {

        inner class ProductImgViewHolder(val binding: ItemMarketDetailBinding):RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImgViewHolder =
            ProductImgViewHolder(ItemMarketDetailBinding.inflate(LayoutInflater.from(parent.context),parent,false))

        override fun onBindViewHolder(holder: ProductDetailActivity.RecyclerViewAdapter.ProductImgViewHolder, position: Int) {
            // ????????? ????????? ??????
            Log.d("????????? ??????????????????", "????????????????????????........")
            Log.d("??????????????????", "${productImgs!![position].pImg}")
            Glide.with(holder.itemView.context)
                .load("${productImgs!![position].pImg}")
                .override(200,200)
                .centerCrop()
                .into(holder.binding.imageViewPD)
        }

        override fun getItemCount(): Int {
            Log.d("getItemCount ????????? ??????", "${productImgs?.size}")
            return productImgs?.size?:0
        }
    }
}