package com.example.readbook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.example.readbook.databinding.ActivityReadlistBinding
import kotlinx.android.synthetic.main.activity_readlist.*

class BookNoteItem(val boottitle: String, val bookcontent:String )

class ReadListActivity : AppCompatActivity() {
    lateinit var binding: ActivityReadlistBinding
    val itemList = arrayListOf<BookNoteItem>()
    val listAdapter = BookListAdapter(itemList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityReadlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvBooknote.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        binding.rvBooknote.adapter = listAdapter




        listAdapter.notifyDataSetChanged()
        buttonregister.setOnClickListener {
            val intent = Intent(this, ListDetailActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}