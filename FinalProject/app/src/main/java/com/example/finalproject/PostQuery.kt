package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.ActionBar
import com.example.finalproject.databinding.ActivitySetAppointmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class PostQuery : AppCompatActivity() {
    public lateinit var actionBar: ActionBar
    public lateinit var binding: ActivitySetAppointmentBinding
    public lateinit var auth: FirebaseAuth
    public lateinit var databaseReference: DatabaseReference

    public lateinit var uid:String
    public lateinit var userId:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_query)

        val cancelPost = findViewById<Button>(R.id.cancelQueryBtn)
        val clearPost = findViewById<Button>(R.id.clearQueryBtn)
        val submitPost = findViewById<Button>(R.id.postBtn)

        cancelPost?.setOnClickListener{
            finish()
        }

        clearPost?.setOnClickListener{
            subj.setText("")
            msg.setText("")
            recipient.setText("")
        }

    }
}