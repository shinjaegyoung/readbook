package com.example.readbook.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dinuscxj.progressbar.CircleProgressBar
import com.example.readbook.model.User
import com.example.readbook.databinding.FragmentHomeBinding
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    lateinit var  binding : FragmentHomeBinding
    var circleProgressBar: CircleProgressBar? = null
    private val DEFAULT_PATTERN = "%d%%"
    companion object{
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }

    private lateinit var database: DatabaseReference
    private var user : ArrayList<User> = arrayListOf()

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
            binding = FragmentHomeBinding.inflate(inflater,container,false)

        circleProgressBar = binding.cpbCirclebar
        circleProgressBar?.setProgress(70)

        return binding.root
        }

        fun format(progress: Int, max: Int): CharSequence {
            return String.format(DEFAULT_PATTERN, (progress.toFloat() / max.toFloat() * 100).toInt())
        }
}