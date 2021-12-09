package com.example.finalproject

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.example.finalproject.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class Profile : AppCompatActivity() {
    private lateinit var binding:ActivityProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var uid:String
    private lateinit var selectedProgram:String
    private lateinit var yearLevel:String

    lateinit var filepath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etgallery.setOnClickListener{
            selectImage()
        }

        auth= FirebaseAuth.getInstance()
        databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Students")
        val firebaseUser=auth.currentUser
        uid=auth.currentUser!!.uid
        databaseReference.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val program = it.child("program").value
                val yearlevel = it.child("yearLevel").value

                if(program != "" && yearlevel != ""){
                    startActivity(Intent(this, StudentHome::class.java))
                    finish()
                }else{
                    val name = it.child("fullname").value
                    binding.username.text = name.toString()
                }
            }
        }

        val programs = resources.getStringArray(R.array.programs)
        val program = findViewById<Spinner>(R.id.program)
        if (program != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, programs)
            program.adapter = adapter

            program.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    if(programs[position] != "--Select--"){
                        selectedProgram = programs[position]
                    }else{
                        selectedProgram = ""
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        binding.btnSave.setOnClickListener{
            yearLevel = binding.etYear.text.toString()
            if(selectedProgram == ""){
                Toast.makeText(this, "Please choose your program!", Toast.LENGTH_LONG).show()
            }else if(yearLevel == "" || yearLevel == null){
                Toast.makeText(this, "Please enter your year level!", Toast.LENGTH_LONG).show()
            }else{
                updateStudent(uid,selectedProgram,yearLevel)
                uploadImage()

                startActivity(Intent(this, StudentHome::class.java))
                finish()
            }
        }
    }

    private fun uploadImage() {
        if(filepath != null){
            var pd = ProgressDialog(this)
            pd.setTitle("Uploading...")
            pd.show()

            storageReference = FirebaseStorage.getInstance("gs://finalproject-7a07c.appspot.com").reference.child("profileImages/$uid")
            storageReference.putFile(filepath)
                .addOnSuccessListener {pO ->
                    pd.dismiss()
                    Toast.makeText(this, "File Uploaded!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{ pO->
                    pd.dismiss()
                    Toast.makeText(this, pO.message, Toast.LENGTH_SHORT).show()
                }.addOnProgressListener { pO->
                    var progress:Double=(100.0 * pO.bytesTransferred/pO.totalByteCount)
                    pd.setMessage("Uploaded ${progress.toInt()}%")
                    pd.dismiss()
                }
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 111 && resultCode == Activity.RESULT_OK && data != null){
            filepath = data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            binding.ivProfilePic.setImageBitmap(bitmap)
        }
    }

    private fun updateStudent(uid: String, selectedProgram: String, yearLevel: String) {
        databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Students")
        val student = mapOf<String, String>(
            "program" to selectedProgram,
            "yearLevel" to yearLevel
        )

        databaseReference.child(uid).updateChildren(student).addOnSuccessListener {
            Toast.makeText(this, "Successfully updated!", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to Update", Toast.LENGTH_LONG).show()
        }
    }
}