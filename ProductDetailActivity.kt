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
private var auth = Firebase.auth

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private var productImgs: ArrayList<Uri>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("lyk","${intent.getStringExtra("pName")}")

        init {
            fireDatabase.child("users")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataSnapshot.getValue
                        }
                        notifyDataSetChanged()
                    })
        }

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        val user = auth.currentUser
        user?.displayName.toString()

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
            .child("${intent.getStringExtra("pid")}")
        Log.d("lyk", "${intent.getStringExtra("pid")}")
        Log.d("lyk","${FirebaseStorage.getInstance().reference.child("productImages").child("${intent.getStringExtra("pid")}")}")


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
                .child("${intent.getStringExtra("pid")}").downloadUrl
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

