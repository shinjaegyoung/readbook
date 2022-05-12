package com.example.readbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readbook.Adapter.MyAdapter
import com.example.readbook.databinding.ActivityProductRegBinding
import com.example.readbook.databinding.ItemMarketRegBinding

class ProductRegActivity : AppCompatActivity() {
    lateinit var binding:ActivityProductRegBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProductRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datas = mutableListOf<String>()
        for(i in 1..4){
            datas.add("Item $i")
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.pImg.layoutManager = layoutManager
        binding.pImg.adapter = MyAdapter(datas)
    }
}

class MyViewHolder(val binding: ItemMarketRegBinding): RecyclerView.ViewHolder(binding.root)

class MyAdapter(val binding: ItemMarketRegBinding):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    = MyViewHolder(ItemMarketRegBinding.inflate(LayoutInflater.from(parent.context),
    parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as MyViewHolder).binding
        binding.pImg.text=datas[position]
    }
}