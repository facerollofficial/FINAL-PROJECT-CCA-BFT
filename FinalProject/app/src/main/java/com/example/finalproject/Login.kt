package com.example.finalproject

import android.app.ProgressDialog.show
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.example.finalproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var email:String
    private lateinit var password:String

    private lateinit var actionBar: ActionBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        checkUser()

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
            show("Email invalid.", "Please check your input and try again.")
        }
        //validate if textfield is empty
        else if((TextUtils.isEmpty(email)||(TextUtils.isEmpty(password)))){
            //do the same as in email, use a different message for binding and show()
            if(TextUtils.isEmpty(email)){
                binding.etEmail.error="Input Email" //displays in the editTextView
                show("Email empty.", "Please input your email.")
            }else{
                binding.etEmail.error="Input Password" //displays in the editTextView
                show("Password empty.", "Please input your password.")
            }
        }else{
            //for valid input, invoke Login function
            show("Please wait.", "Logging in....")
            firebaselogin()
        }
    }

    private fun firebaselogin() {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                //when login credentials are successfully validated
                //start by getting the users acct information from firebase
                val firebaseUser=auth.currentUser
                val email=firebaseUser!!.email
                show("Welcome","$email")

                //redirect user to profile page
                startActivity(Intent(
                    this,
                    Profile::class.java))
                finish() //closes this activity
            }.addOnFailureListener { e->
                //when login credentials are not found
                // e.message shows cause of error
                show("Login Failed.","${e.message}")
            }
    }

    private fun show(title:String, message:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK", null) //user needs to click on "Ok" Button to dismiss
        builder.setCancelable(false)    //does not allow dialog to be cancelled

        val dialog=builder.create()
        dialog.show()
    }

    private fun checkUser(){
        val firebaseUser=auth.currentUser
        if(firebaseUser != null){
            startActivity(Intent(
                this,
                Profile::class.java
            ))
            finish()
        }
    }
}