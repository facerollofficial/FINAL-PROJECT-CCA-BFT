package com.example.finalproject

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import com.example.finalproject.databinding.ActivityProfileTeacherBinding
import com.example.finalproject.databinding.ActivityTeacherProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileTeacher : AppCompatActivity() {
    private lateinit var binding: ActivityProfileTeacherBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var uid:String
    lateinit var filepath: Uri
    var clicked:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()
        databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Instructors")
        val firebaseUser=auth.currentUser
        uid=auth.currentUser!!.uid

        binding.galleryBtn.setOnClickListener{
            selectImage()
            clicked = true
        }

        binding.btnSave.setOnClickListener{
            if(filepath != null){
                uploadImage()

                startActivity(Intent(this, TeacherHome::class.java))
                finish()
            }else{
                Toast.makeText(this,"Please upload profile image first!",Toast.LENGTH_SHORT).show()
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
}