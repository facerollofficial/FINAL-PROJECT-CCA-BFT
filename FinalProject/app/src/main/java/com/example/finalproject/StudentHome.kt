package com.example.finalproject

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.finalproject.databinding.ActivityProfileBinding
import com.example.finalproject.databinding.ActivityStudentHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class StudentHome : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding:ActivityStudentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var actionBar: ActionBar
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var uid:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //bind variables
        val drawerLayout: DrawerLayout =findViewById(R.id.myDrawerLayout)
        val navView: NavigationView =findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        actionBar=supportActionBar!!
        actionBar.title="Home"

        auth= FirebaseAuth.getInstance()
        val firebaseUser=auth.currentUser
        uid=auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance("gs://finalproject-7a07c.appspot.com").reference.child("profileImages/$uid")

        val localfile= File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            var profImage = findViewById<ImageView>(R.id.profileImage)
            profImage.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to retrieve profile Image", Toast.LENGTH_SHORT).show()
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home-> Toast.makeText(this, "Welcome back to the page!", Toast.LENGTH_LONG).show()
                R.id.profile->{
                    startActivity(Intent(this, StudentProfile::class.java))
                    finish()
                }

                R.id.logout-> {
                    auth.signOut()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }
                else-> Toast.makeText(this, "Please click existing items", Toast.LENGTH_LONG).show()
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}