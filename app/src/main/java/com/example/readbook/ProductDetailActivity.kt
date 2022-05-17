package com.example.readbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readbook.databinding.ActivityProductDetailBinding
import com.example.readbook.databinding.ItemMarketDetailBinding
import com.example.readbook.fragment.MarketFragment
import com.example.readbook.model.ProductImg
import com.example.readbook.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.ArrayList

private val fireDatabase = FirebaseDatabase.getInstance().reference
private lateinit var productImgs: ArrayList<ProductImg>

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("lyk","${intent.getStringExtra("pid")}")

        fireDatabase.child("users").child("${intent.getStringExtra("user")}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue<User>()
                    Glide.with(binding.profilePD.context).load(user?.profileImageUrl)
                        .apply(RequestOptions().circleCrop())
                        .into(binding.profilePD)
                    binding.usernamePD.text = user?.name
                }
            })

        // db에 저장된 이미지 uri 가져오기
        productImgs = ArrayList<ProductImg>()

        fireDatabase.child("productImg").child("${intent.getStringExtra("pid")}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("lyk","fail..............")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    productImgs!!.clear()
                    Log.d("lyk","${fireDatabase.child("productImg").child("${intent.getStringExtra("pid")}")}")
                    Log.d("lyk","success..............")
                    Log.d("lyk", "${snapshot.value}")
                    for (data in snapshot.children) {
                        productImgs!!.add(data.getValue<ProductImg>()!!)
                        println(data)
                    }
                }
            })

        binding.tvProductName.text=intent.getStringExtra("pName")
        binding.tvProductPrice.text=intent.getStringExtra("pPrice")
        binding.tvProductDes.text=intent.getStringExtra("pDes")
        binding.usernamePD.text=intent.getStringExtra("user")
        binding.tvStatus.text=intent.getStringExtra("status")

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, MarketFragment::class.java)
            startActivity(intent)
        }

        //RecyclerViewAdapter
        val layoutManager = LinearLayoutManager(this@ProductDetailActivity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recyclerViewPD.layoutManager=layoutManager
        binding.recyclerViewPD.adapter= RecyclerViewAdapter()
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<ProductDetailActivity.RecyclerViewAdapter.ProductImgViewHolder>() {

        inner class ProductImgViewHolder(val binding: ItemMarketDetailBinding):RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImgViewHolder =
            ProductImgViewHolder(ItemMarketDetailBinding.inflate(LayoutInflater.from(parent.context),parent,false))

        override fun onBindViewHolder(holder: ProductImgViewHolder, position: Int) {
                Glide.with(holder.itemView.context)
                    .load(productImgs!![position])
                    .override(200,200)
                    .centerCrop()
                    .into(holder.binding.imageViewPD)
        }

        override fun getItemCount(): Int {
            //Log.d("lyk","${productImgs}")
            return productImgs!!.size?:0
        }
    }
}