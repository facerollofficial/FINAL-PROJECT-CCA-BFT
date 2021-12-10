package com.example.finalproject

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.finalproject.databinding.ActivityTeacherHomeBinding
import com.example.finalproject.databinding.ActivityTeacherProfileBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class TeacherProfile : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth
    private lateinit var actionBar: ActionBar
    private lateinit var binding: ActivityTeacherProfileBinding

    private lateinit var uid:String
    lateinit var filepath: Uri
    var didClick = false

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var checkImage: StorageReference

    private lateinit var fullname:EditText
    private lateinit var employeeId:EditText
    private lateinit var saveBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var editBtn: Button
    private lateinit var cameraPic: ImageView
    private lateinit var navImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout: DrawerLayout =findViewById(R.id.myDrawerLayout2)
        val navView2: NavigationView =findViewById(R.id.nav_view2)
        saveBtn = findViewById<Button>(R.id.saveBtn)
        cancelBtn=findViewById<Button>(R.id.cancelBtn)
        cameraPic=findViewById(R.id.cameraPic)
        editBtn=findViewById<Button>(R.id.editBtn)
        fullname = findViewById<EditText>(R.id.inFullname)
        employeeId = findViewById<EditText>(R.id.inId)


        toggle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        actionBar=supportActionBar!!
        actionBar.title="Instructor"

        cameraPic.visibility = View.GONE
        saveBtn.visibility= View.GONE
        cancelBtn.visibility= View.GONE

        fullname.isEnabled = false;
        employeeId.isEnabled = false;

        auth= FirebaseAuth.getInstance()
        val firebaseUser=auth.currentUser
        uid=auth.currentUser!!.uid
        storageReference = FirebaseStorage.getInstance("gs://finalproject-7a07c.appspot.com").reference.child("profileImages/$uid")
        checkImage = FirebaseStorage.getInstance("gs://finalproject-7a07c.appspot.com").getReference("profileImages/")
        databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Instructors")
        databaseReference.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val name = it.child("fullName").value
                val empId = it.child("userId").value
                fullname.setText(name.toString())

                employeeId.setText(empId.toString())

                fullname.isEnabled = false;
                employeeId.isEnabled = false;
            }
        }


        val localfile= File.createTempFile("tempImage", "jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            var profImage = findViewById<ImageView>(R.id.navImageTeacher)
            var profilePic = findViewById<ImageView>(R.id.profilePicture)
            //profImage.setImageBitmap(bitmap)
            profilePic.setImageBitmap(bitmap)
        }

        navView2.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home-> Toast.makeText(this, "Welcome back to the page!", Toast.LENGTH_LONG).show()
                R.id.logout-> {
                    auth.signOut()
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }
                R.id.profileTeacher->{
                    startActivity(Intent(this, TeacherProfile::class.java))
                }
                R.id.sendEmail->{
                    startActivity(Intent(this, Email::class.java))
                }
                R.id.postManage->{
                    startActivity(Intent(this, ManagePosts::class.java))
                }
                R.id.postListShow->{
                    startActivity(Intent(this, PostsList::class.java))
                }
                R.id.viewStudents->{
                    startActivity(Intent(this, UsersList::class.java))
                }
                else-> Toast.makeText(this, "Please click existing items", Toast.LENGTH_LONG).show()
            }
            true
        }


        //Button Presses

        binding.backButton.setOnClickListener{
            finish()
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
            binding.backButton.visibility=View.GONE

            fullname.isEnabled = true;
            employeeId.isEnabled = true;
        }

        cancelBtn?.setOnClickListener{
            cameraPic.visibility=View.GONE
            saveBtn.visibility= View.GONE
            cancelBtn.visibility=View.GONE
            editBtn.visibility = View.VISIBLE
            binding.backButton.visibility=View.VISIBLE

            fullname.isEnabled = false;
            employeeId.isEnabled = false;
        }

        saveBtn?.setOnClickListener{
            cameraPic.visibility=View.GONE
            saveBtn.visibility= View.GONE
            cancelBtn.visibility=View.GONE
            editBtn.visibility = View.VISIBLE
            binding.backButton.visibility=View.VISIBLE

            fullname.isEnabled = false;
            employeeId.isEnabled = false;

            var name = binding.inFullname.text.toString().trim()
            var id = binding.inId.text.toString().trim()

            save(name, id)
            if(didClick == true){
                changePic()
            }
            Toast.makeText(this, "Profile Successfully updated", Toast.LENGTH_SHORT).show()
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

    private fun save(name: String, id: String) {
        databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Instructors")
        val teacher = mapOf<String, String>(
            "fullName" to name,
            "userId" to id,
        )
        uid=auth.currentUser!!.uid.toString()
        databaseReference.child(uid).updateChildren(teacher).addOnSuccessListener {
            Toast.makeText(this, "Profile successfully updated!", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to update profile!", Toast.LENGTH_LONG).show()
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


}