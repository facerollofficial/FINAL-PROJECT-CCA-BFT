package com.example.finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.RadioGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import com.example.finalproject.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {
    private lateinit var binding:ActivitySignupBinding
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var userId:String
    private lateinit var fullname:String
    private lateinit var role:String
    private lateinit var radioGroup: RadioGroup

    private lateinit var actionBar: ActionBar

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        radioGroup = binding.radioGroup
        radioGroup.setOnCheckedChangeListener{radioGroup, checkedId ->
            if(checkedId == R.id.teacher){
                role = "Instructor"
            }
            if(checkedId == R.id.student){
                role = "Student"
            }
        }

        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        userId = binding.etId.text.toString().trim()
        fullname = binding.etId.text.toString().trim()

        actionBar=supportActionBar!!
        actionBar.title=("Signup")

        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()


        binding.btnSignup.setOnClickListener{
            auth = FirebaseAuth.getInstance()
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
        userId = binding.etId.text.toString().trim()
        fullname = binding.etFullname.text.toString().trim()

        //validate input
        //Email validation | checks email input pattern
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error="Invalid email" //displays in the editTextView
            show("Email invalid.", "Please check your input and try again.")
        }
        //validate if textfield is empty
        else if((TextUtils.isEmpty(email)||(TextUtils.isEmpty(password))||(TextUtils.isEmpty(fullname))||(TextUtils.isEmpty(userId)))){
            //do the same as in email, use a different message for binding and show()
            if(TextUtils.isEmpty(email)){
                binding.etEmail.error="Input Email" //displays in the editTextView
                show("Email empty.", "Please input your email.")
            }else if(TextUtils.isEmpty(password)){
                binding.etPassword.error="Input Password" //displays in the editTextView
                show("Password empty.", "Please input your password.")
            }else if(TextUtils.isEmpty(userId)){
                binding.etId.error="Input ID" //displays in the editTextView
                show("User Id empty.", "Please input your ID.")
            }else{
                binding.etFullname.error="Input Full Name" //displays in the editTextView
                show("Name empty.", "Please input your full name.")
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
        userId = binding.etId.text.toString().trim()
        fullname = binding.etFullname.text.toString().trim()

        radioGroup = binding.radioGroup
        radioGroup.setOnCheckedChangeListener{radioGroup, checkedId ->
            if(checkedId == R.id.teacher){
                role = "Instructor"
            }
            if(checkedId == R.id.student){
                role = "Student"
            }
        }

        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //add user account details to firebase ->from email and password input
                val firebaseUser=auth.currentUser
                val currentUid=auth.currentUser?.uid
                val email=firebaseUser!!.email

                if(role == "Instructor"){
                    databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Instructors")
                    val newTeacher = Teacher(role, userId, fullname)
                    if(currentUid!=null){
                        databaseReference.child(currentUid).setValue(newTeacher).addOnCompleteListener {
                        }
                    }
                }else if(role == "Student"){
                    databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Students")
                    val newStudent = Student(role, userId, fullname, "", "")
                    if(currentUid!=null){
                        databaseReference.child(currentUid).setValue(newStudent).addOnCompleteListener {
                            //alert if account is created
                            show("Success", "Account created, welcome $email")

                            //redirect user to his/her default page or profile page
                            startActivity(Intent(this, Profile::class.java))
                            finish()
                        }
                    }
                }


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