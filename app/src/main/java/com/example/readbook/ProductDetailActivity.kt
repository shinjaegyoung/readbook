package com.example.readbook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readbook.databinding.ActivityProductDetailBinding
import com.example.readbook.databinding.ItemMarketDetailBinding
import com.example.readbook.fragment.MarketFragment
import com.example.readbook.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.util.ArrayList

private val fireDatabase = FirebaseDatabase.getInstance().reference

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private var productImgs: ArrayList<Uri>? = null
    private val pid=intent.getStringExtra("pid")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvProductName.text=intent.getStringExtra("pName")
        binding.tvProductPrice.text=intent.getStringExtra("pPrice")
        binding.tvProductDes.text=intent.getStringExtra("pDes")
        binding.usernamePD.text=intent.getStringExtra("user")
        binding.tvStatus.text=intent.getStringExtra("status")

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, MarketFragment::class.java)
            startActivity(intent)
        }

        productImgs = ArrayList<Uri>()
        FirebaseStorage.getInstance().reference.child("productImages")
            .child("${pid}")
        Log.d("lyk", "${pid}")
        Log.d("lyk","${FirebaseStorage.getInstance().reference.child("productImages").child("${pid}")}")


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
            // storage에 저장된 이미지 가져오기
            val binding=(holder).binding
            FirebaseStorage.getInstance().reference.child("productImages")
                .child("${pid}").downloadUrl
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Glide.with(holder.itemView.context)
                            .load(task.result)
                            .override(200,200)
                            .centerCrop()
                            .into(holder.binding.imageViewPD)
                    }
                }
            }

        override fun getItemCount(): Int {
            return productImgs?.size?:0
        }
    }
}

