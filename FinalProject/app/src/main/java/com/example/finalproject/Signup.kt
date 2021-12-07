package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.example.finalproject.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class Signup : AppCompatActivity() {
    private lateinit var binding:ActivitySignupBinding
    private lateinit var email:String
    private lateinit var password:String

    private lateinit var actionBar: ActionBar

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()

        actionBar=supportActionBar!!
        actionBar.title=("Signup")

        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        binding.btnSignup.setOnClickListener{
            //invoke the signup function
            firebaseSignUp()
        }
        binding.tvLogin.setOnClickListener{
            //if the user already has an account, validate
            validateAccount()
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

    private fun validateAccount() {
        email=binding.etEmail.text.toString().trim()
        password= binding.etPassword.text.toString().trim()

        //validate input
        //Email validation | checks email input pattern
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
        TODO("Not yet implemented")
    }

    private fun firebaseSignUp() {
        email=binding.etEmail.text.toString().trim()
        password= binding.etPassword.text.toString().trim()

        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //add user account details to firebase ->from email and password input
                val firebaseUser=auth.currentUser
                val email=firebaseUser!!.email

                //alert if account is created
                show("Success", "Account created, welcome $email")

                //redirect user to his/her default page or profile page
                startActivity(Intent(this, Profile::class.java))
                finish()
            }
            //failed Signup
            .addOnFailureListener { e->
                show("Signup Failed!", "Signup process encountered an error. "+
                        "${e.message}")
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        //for action bar
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}