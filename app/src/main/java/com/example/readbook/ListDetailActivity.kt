package com.example.readbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.readbook.model.BookNote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list_detail.*
import kotlinx.android.synthetic.main.activity_readlist.*

private lateinit var auth: FirebaseAuth
class ListDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_detail)

        auth = Firebase.auth
        database = Firebase.database.reference

        val useremail =  Firebase.auth.currentUser?.email.toString()
        val booktitle = findViewById<EditText>(R.id.booktitle).text
        val bookcontent = findViewById<EditText>(R.id.bookcontent).text


       /* var useremailarr = useremail.split(".")
        var useremail1 = ""

        for(i in 0 .. useremailarr.size-1 step(1)){
            useremail1 += useremailarr.get(i)
        }*/

        var useremail_plus = useremail.replace(".", "+")

        booknotesavebutton.setOnClickListener {

            val booknote = BookNote(
                booktitle.toString(),
                bookcontent.toString()
            )
            //database.child("bookdiary").push()
               // .setValue(booknote)
            //database.get().=setValue(booknote)
            database.child("bookdiary").child(useremail_plus).setValue(booknote)




        }
    }


}