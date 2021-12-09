package com.example.finalproject

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.finalproject.databinding.ActivityStudentProfileBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class StudentProfile : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var actionBar: ActionBar
    private lateinit var binding:ActivityStudentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var uid:String
    private lateinit var fullname:EditText
    private lateinit var studentId:EditText
    private lateinit var level:EditText
    private lateinit var saveBtn:Button
    private lateinit var cancelBtn:Button
    private lateinit var editBtn:Button
    private lateinit var cameraPic:ImageView

    lateinit var filepath: Uri
    var didClick = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar=supportActionBar!!
        actionBar.title="Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //bind variables
        val drawerLayout: DrawerLayout =findViewById(R.id.ProfileDrawer)
        val navView: NavigationView =findViewById(R.id.nav_view)
        fullname = findViewById<EditText>(R.id.inFullname)
        studentId = findViewById<EditText>(R.id.inId)
        level = findViewById<EditText>(R.id.inYearlevel)
        saveBtn = findViewById<Button>(R.id.saveBtn)
        cancelBtn=findViewById<Button>(R.id.cancelBtn)
        editBtn=findViewById<Button>(R.id.editBtn)
        cameraPic=findViewById(R.id.cameraPic)

        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        saveBtn.visibility= View.GONE
        cancelBtn.visibility=View.GONE
        cameraPic.visibility=View.GONE

        auth= FirebaseAuth.getInstance()
        val firebaseUser=auth.currentUser
        uid=auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance("gs://finalproject-7a07c.appspot.com").reference.child("profileImages/$uid")
        databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Students")
        databaseReference.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val name = it.child("fullname").value
                val studId = it.child("userId").value
                val lvl = it.child("yearLevel").value

                fullname.setText(name.toString())
                studentId.setText(studId.toString())
                level.setText(lvl.toString())

                fullname.isEnabled = false;
                studentId.isEnabled = false;
                level.isEnabled = false;
            }
        }

        val localfile= File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            var profImage = findViewById<ImageView>(R.id.profileImage)
            var profilePic = findViewById<ImageView>(R.id.profilePicture)
            profImage.setImageBitmap(bitmap)
            profilePic.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to retrieve profile Image", Toast.LENGTH_SHORT).show()
        }



        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home-> Toast.makeText(this, "Welcome back to the page!", Toast.LENGTH_LONG).show()
                R.id.profile->{
                    startActivity(Intent(this, StudentProfile::class.java))
                    finish()
                }
                R.id.setAppointment->{
                    startActivity(Intent(this, SetAppointment::class.java))
                    finish()
                }
                R.id.sendEmail->{
                    startActivity(Intent(this, Email::class.java))
                    finish()
                }
                R.id.postQuery->{
                    startActivity(Intent(this, PostQuery::class.java))
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

        cameraPic?.setOnClickListener{
            selectImage()
            didClick = true
        }

        editBtn?.setOnClickListener{
            cameraPic.visibility=View.VISIBLE
            editBtn.visibility= View.GONE
            saveBtn.visibility= View.VISIBLE
            cancelBtn.visibility=View.VISIBLE

            fullname.isEnabled = true;
            studentId.isEnabled = true;
            level.isEnabled = true;
        }

        cancelBtn?.setOnClickListener{
            cameraPic.visibility=View.GONE
            saveBtn.visibility= View.GONE
            cancelBtn.visibility=View.GONE
            editBtn.visibility = View.VISIBLE

            fullname.isEnabled = false;
            studentId.isEnabled = false;
            level.isEnabled = false;
        }

        saveBtn?.setOnClickListener{
            cameraPic.visibility=View.GONE
            saveBtn.visibility= View.GONE
            cancelBtn.visibility=View.GONE
            editBtn.visibility = View.VISIBLE

            fullname.isEnabled = false;
            studentId.isEnabled = false;
            level.isEnabled = false;

            var name = binding.inFullname.text.toString().trim()
            var yearLvl = binding.inYearlevel.text.toString().trim()
            var studId = binding.inId.text.toString().trim()

            save(name, yearLvl, studId)
            if(didClick == true){
                changePic()
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
            binding.profilePicture.setImageBitmap(bitmap)
        }
    }

    private fun changePic() {
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

    private fun save(name: String, yearLvl: String, studId: String) {
        databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Students")
        val student = mapOf<String, String>(
            "fullname" to name,
            "yearLevel" to yearLvl,
            "userId" to studId
        )

        databaseReference.child(uid).updateChildren(student).addOnSuccessListener {
            Toast.makeText(this, "Profile successfully updated!", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to update profile!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}