package com.example.readbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookListAdapter ( val itemList : ArrayList<BookNoteItem>): RecyclerView.Adapter<BookListAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val booktitle: TextView = itemView.findViewById(R.id.rc_booktitle)
        val bookcontent: TextView = itemView.findViewById(R.id.rc_bookcontent)
    } //레이아웃 내 View 연결

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return ViewHolder(view)
    } //Item layout 과 결합

    override fun onBindViewHolder(holder: BookListAdapter.ViewHolder, position: Int) {
        holder.booktitle.text = itemList[position].boottitle
        holder.bookcontent.text=itemList[position].bookcontent
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}
