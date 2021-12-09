package com.example.finalproject

import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.example.finalproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var email:String
    private lateinit var password:String

    private lateinit var actionBar: ActionBar

    private lateinit var uid:String
    private var role:String = ""

    private lateinit var auth: FirebaseAuth
    private lateinit var studentReference: DatabaseReference
    private lateinit var teacherReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val firebaseUser=auth.currentUser
        uid=auth.currentUser?.uid.toString()

        checkStudent()
        checkTeacher()

        actionBar=supportActionBar!!
        actionBar.title="Login"

        binding.loginBtn.setOnClickListener{
            validateData()
        }

        binding.tvSignup.setOnClickListener{
            navToSignup()
        }

    }

    private fun navToSignup() {
        startActivity(Intent(this, Signup::class.java))
    }

    private fun validateData() {
        email=binding.etEmail.text.toString().trim()
        password= binding.etPassword.text.toString().trim()

        //validate input
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error="Invalid email" //displays in the editTextView
            Toast.makeText(this,"Invalid Email! Please check your input and try again.",Toast.LENGTH_SHORT).show()
        }
        //validate if textfield is empty
        else if((TextUtils.isEmpty(email)||(TextUtils.isEmpty(password)))){

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this,"Please fill in all fields!",Toast.LENGTH_SHORT).show()
            }
        }else{
            //for valid input, invoke Login function
            Toast.makeText(this,"Logging in...",Toast.LENGTH_SHORT).show()
            firebaselogin()
        }
    }

    private fun firebaselogin() {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                checkStudent()
                checkTeacher()

            }.addOnFailureListener { e->
                //when login credentials are not found
                // e.message shows cause of error
                Toast.makeText(this,"Login Failed!",Toast.LENGTH_SHORT).show()
            }
    }



    private fun checkStudent(){
        val firebaseUser=auth.currentUser
        if(firebaseUser != null){
            uid= auth.currentUser!!.uid
            studentReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Students")
            studentReference.child(uid).get().addOnSuccessListener {
                if(it.exists()) {
                    startActivity(Intent(this, StudentHome::class.java))
                    finish()
                }
            }
        }
    }

    private fun checkTeacher(){
        val firebaseUser=auth.currentUser
        if(firebaseUser != null){
            uid= auth.currentUser!!.uid
            teacherReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Instructors")
            teacherReference.child(uid).get().addOnSuccessListener {
                if(it.exists()) {
                    startActivity(Intent(this, TeacherHome::class.java))
                    finish()
                }
            }
        }
    }
}