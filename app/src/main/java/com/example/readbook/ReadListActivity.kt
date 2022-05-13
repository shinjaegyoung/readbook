package com.example.readbook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.readbook.databinding.ActivityReadlistBinding
import kotlinx.android.synthetic.main.activity_readlist.*

class ReadListActivity : AppCompatActivity() {
    lateinit var binding: ActivityReadlistBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityReadlistBinding.inflate(layoutInflater)
        setContentView(binding.root)


        buttonregister.setOnClickListener {
            val intent = Intent(this, ListDetailActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}