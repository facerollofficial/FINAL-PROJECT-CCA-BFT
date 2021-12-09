package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.finalproject.databinding.ActivityPostQueryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PostQuery : AppCompatActivity() {
    public lateinit var actionBar: ActionBar
    public lateinit var binding: ActivityPostQueryBinding
    public lateinit var auth: FirebaseAuth
    public lateinit var databaseReference: DatabaseReference
    public lateinit var studentReference: DatabaseReference

    public lateinit var uid:String
    public var userId:String = ""

    private lateinit var title: EditText
    private lateinit var details: EditText
    private lateinit var course: EditText
    private lateinit var email: String
    private var countNumber : Int = 1
    private var isEmp: Int = 0
    private var fieldEmpty: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostQueryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cancelPost = findViewById<Button>(R.id.cancelQueryBtn)
        val clearPost = findViewById<Button>(R.id.clearQueryBtn)
        val submitPost = findViewById<Button>(R.id.postBtn)

        title = findViewById(R.id.queryTitle)
        details = findViewById(R.id.queryDetails)
        course = findViewById(R.id.queryCourse)

        //title, details, course, userEmail, incrementing

        auth= FirebaseAuth.getInstance()
        val firebaseUser=auth.currentUser
        uid=auth.currentUser!!.uid
        email= auth.currentUser!!.email.toString()


        cancelPost?.setOnClickListener{
            finish()
        }

        clearPost?.setOnClickListener{
            title.setText("")
            details.setText("")
            course.setText("")
        }

        submitPost?.setOnClickListener{
            val title = title.text.toString().trim()
            val details = details.text.toString().trim()
            val course = course.text.toString().trim()
            checkFields(title, details, course, email)

            if(fieldEmpty == false){
                getUserId(title, details, course, email)

                //checkLastPostNumber(title, details, course, email)
            }
        }

    }

    private fun checkFields(title: String, details: String, course: String, email: String) {
        if(title == "" ||details == "" ||course == ""){
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            fieldEmpty = true
        }else{
            fieldEmpty = false
        }
    }


    fun getUserId(title: String, details:String, course: String, userEmail:String){
        studentReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Students")
        uid= auth.currentUser!!.uid
        studentReference.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val program = it.child("userId").value
                userId= program.toString()

                checkLastPostNumber(userId, title, details, course, email)
            }
        }
    }

    fun checkLastPostNumber(userId: String, title: String, details:String, course: String, userEmail:String){
        databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Posts")
        val newPost = Post(title, details, course, userEmail)
        var x = countNumber.toString()

        databaseReference.child("$title ($userId)").setValue(newPost)
        Toast.makeText(this, "Post successful!", Toast.LENGTH_SHORT).show()
    }

}