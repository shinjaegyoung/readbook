package com.example.readbook.Adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readbook.databinding.ItemMarketRegBinding
import com.example.readbook.model.ProductList

class MarketRegAdapter(private val products : List<ProductList>): RecyclerView.Adapter<MarketRegAdapter.MarketRegViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketRegViewHolder {
        return MarketRegViewHolder(ItemMarketRegBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MarketRegViewHolder, position: Int) {
        holder.adapterPosition
    }

    override fun getItemCount(): Int {
        return 4
    }

    class MarketRegViewHolder(private val binding: ItemMarketRegBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(products: ProductList.Product) {
            Bitmap bm = BitmapFactory.decodeStream(products.pImg[position].toString())
            binding.pImg.setImageBitmap(bm)
        }
    }
}