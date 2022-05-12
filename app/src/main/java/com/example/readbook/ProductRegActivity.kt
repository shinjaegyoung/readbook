package com.example.readbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.readbook.databinding.ActivityProductRegBinding

class ProductRegActivity : AppCompatActivity() {
    lateinit var binding:ActivityProductRegBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProductRegBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}