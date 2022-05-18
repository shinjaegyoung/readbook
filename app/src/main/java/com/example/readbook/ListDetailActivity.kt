package com.example.readbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.set
import com.example.readbook.databinding.ActivityListDetailBinding
import com.example.readbook.fragment.ProfileFragment
import com.example.readbook.model.BookNote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list_detail.*
import kotlinx.android.synthetic.main.activity_readlist.*
import kotlinx.android.synthetic.main.book_item.*

private lateinit var auth: FirebaseAuth
class ListDetailActivity : AppCompatActivity() {
    private val BookNote = BookNote()
    val useremail =  Firebase.auth.currentUser?.email.toString()
    var useremail_plus = useremail.replace(".", "+")
    val user = Firebase.auth.currentUser
    //currentUser = 로그인한 사용자
    val userId = user?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityListDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.booktitle.setText( intent.getStringExtra("title"))
        binding.bookcontent.setText(intent.getStringExtra("content"))

        auth = Firebase.auth
        database = Firebase.database.reference

        val useremail =  Firebase.auth.currentUser?.email.toString()
        val booktitle = findViewById<EditText>(R.id.booktitle).text
        val bookcontent = findViewById<EditText>(R.id.bookcontent).text
        val user = Firebase.auth.currentUser
        val fireDatabase = FirebaseDatabase.getInstance()
        val new2Ref = fireDatabase.getReference("bookdiray").child(useremail_plus).push()
        BookNote.bookid = new2Ref.key.toString()
        //currentUser = 로그인한 사용자
        val userId = user?.uid
        val userIdSt = userId.toString()
        var useremailarr = useremail.split(".")
        var useremail1 = ""

        for(i in 0 .. useremailarr.size-1 step(1)){
            useremail1 += useremailarr.get(i)
        }

        var useremail_plus = useremail.replace(".", "+")





       bookdetaildelete.setOnClickListener {
            Log.d("keykey11111", "${intent.getStringExtra("bookid")}")

            FirebaseDatabase.getInstance().getReference("bookdiary")
                .child("${useremail_plus}").child("${intent.getStringExtra("bookid")}").removeValue()

           val intent = Intent(this@ListDetailActivity , ReadListActivity::class.java)
           startActivity(intent)
           finish()
       }

        bookdetailupdate?.setOnClickListener{
            BookNote.booktitle = binding.booktitle.text.toString()
            BookNote.bookcontent = binding.bookcontent.text.toString()

            database.child("bookdiary").child("${useremail_plus}").child("${intent.getStringExtra("bookid")}").removeValue()


            val booknote = BookNote(
                binding.booktitle.text.toString(),
                binding.bookcontent.text.toString()
            )

            database.child("bookdiary").child(useremail_plus).child("${intent.getStringExtra("bookid")}").setValue(booknote)

            val intent = Intent(this@ListDetailActivity , ReadListActivity::class.java)
            startActivity(intent)
            finish()
        }


        }









    }






