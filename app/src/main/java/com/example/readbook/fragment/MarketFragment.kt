package com.example.readbook.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readbook.MessageActivity
import com.example.readbook.ProductRegActivity
import com.example.readbook.R
import com.example.readbook.databinding.FragmentMarketBinding
import com.example.readbook.model.Product
import com.example.readbook.model.ProductList
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

private val productImg = Product.ProductImg()

class MarketFragment : Fragment() {
    lateinit var binding: FragmentMarketBinding
    companion object {
        fun newInstance(): MarketFragment {
            return MarketFragment()
        }
    }
    private val fireDatabase = FirebaseDatabase.getInstance().reference

    // 메모리에 올라감
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMarketBinding.inflate(layoutInflater, container, false)
        //val view = inflater.inflate(R.layout.fragment_market, container, false)
        //val recyclerView = view.findViewById<RecyclerView>(R.id.marketfragment_recyclerview)
        binding.marketfragmentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.marketfragmentRecyclerview.adapter = RecyclerViewAdapter()

        binding.btnRegMarket.setOnClickListener {
            val intent=Intent(context, ProductRegActivity::class.java)
            context?.startActivity(intent)
        }

        return binding.root
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        private val productList = ArrayList<ProductList>()
        private var uid: String? = null
        private var pid: String? = null
        private val destinationUsers: ArrayList<String> = arrayListOf()

        init {
            uid = Firebase.auth.currentUser?.uid.toString()
            println(uid)

            fireDatabase.child("product_list").orderByChild("product/$pid").equalTo(true)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        productList.clear()
                        for (data in snapshot.children) {
                            productList.add(data.getValue<ProductList>()!!)
                            println(data)
                        }
                        notifyDataSetChanged()
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            return CustomViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)
            )
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.productImg)
            val textView_title: TextView = itemView.findViewById(R.id.tvProductName)
            val textView_price: TextView = itemView.findViewById(R.id.tvPrice)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            var destinationUid: String? = null
            //상품 리스트에서 상품 체크
            for (user in productList[position].products.keys) {
                if (!user.equals(uid)) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }
            fireDatabase.child("product").child("$destinationUid")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        //                         //
                        val product = snapshot.getValue<Product>()
                        Glide.with(holder.itemView.context)
                            .load(productImg.pImg)
                            .override(200,200)
                            .into(holder.imageView)
                        holder.textView_title.text = product?.pName
                        holder.textView_price.text = product?.pPrice.toString()
                    }
                })

            //상품 선택 시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                context?.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return productList.size
        }
    }
}