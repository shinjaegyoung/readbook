package com.example.readbook.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.readbook.databinding.FragmentMarketBinding

class MarketFragment : Fragment() {
    lateinit var binding: FragmentMarketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setSupportActionBar(binding.marketfragmentToolbar)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMarketBinding.inflate(inflater, container, false)
        return binding.root
    }
}