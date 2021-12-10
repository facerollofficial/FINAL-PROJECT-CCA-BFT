package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.finalproject.databinding.ActivityManagePostsBinding
import com.example.finalproject.databinding.ActivityStudentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ManagePosts : AppCompatActivity() {
    private lateinit var actionBar: ActionBar
    private lateinit var binding:ActivityManagePostsBinding

    public lateinit var auth: FirebaseAuth
    public lateinit var studentReference: DatabaseReference
    public lateinit var teacherReference: DatabaseReference
    public lateinit var postReference: DatabaseReference

    private lateinit var uid: String
    private lateinit var role: String


    private lateinit var title: EditText
    private lateinit var sender: EditText

    private lateinit var searchBtn: Button
    private lateinit var sendBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var details: EditText
    private lateinit var reply: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_posts)
        binding = ActivityManagePostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar=supportActionBar!!
        actionBar.title="Manage Post"

        title = findViewById(R.id.managePostTitle)
        sender = findViewById(R.id.managePostUser)
        reply = findViewById(R.id.managePostReply)
        details = findViewById(R.id.managePostDetails)

        sendBtn = findViewById(R.id.sendBtn)
        deleteBtn = findViewById(R.id.manageDeletePost)
        searchBtn = findViewById(R.id.managePostSearch)

        auth= FirebaseAuth.getInstance()
        val firebaseUser=auth.currentUser
        uid=auth.currentUser!!.uid

        reply.isEnabled = false;
        details.isEnabled = false;
        sender.isEnabled = false;
        sendBtn.visibility= View.GONE
        deleteBtn.visibility= View.GONE

        teacherReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Instructors")
        teacherReference.child(uid).get().addOnSuccessListener {

            if(it.exists()){
                val role2 = it.child("role").value
                role = role2.toString()

                binding.managePostSearch.setOnClickListener {
                    var name = binding.managePostTitle.text.toString().trim()
                    if(name == ""){
                        Toast.makeText(this, "Please enter a Title!", Toast.LENGTH_SHORT).show()
                    }else{
                        postReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Posts")
                        postReference.child(name).get().addOnSuccessListener {
                            if(it.exists()){
                                val title1 = it.child("title").value.toString()
                                val sender1 = it.child("userEmail").value.toString()
                                val details1 = it.child("details").value.toString()
                                val reply = it.child("reply").value.toString()

                                binding.managePostUser.setText(sender1.toString())
                                binding.managePostDetails.setText(details1.toString())
                                binding.managePostReply.setText(reply.toString())

                                binding.managePostReply.isEnabled = true;

                                sendBtn?.setOnClickListener {
                                    var reply = binding.managePostReply.text.toString().trim()
                                    if(reply == ""){
                                        Toast.makeText(this, "Please enter a reply!", Toast.LENGTH_SHORT).show()
                                    }else{
                                        replySend(reply, title1)
                                    }
                                }

                                deleteBtn?.setOnClickListener {
                                    binding.managePostTitle.setText("")
                                    binding.managePostUser.setText("")
                                    binding.managePostDetails.setText("")
                                    binding.managePostReply.setText("")

                                    binding.managePostReply.isEnabled = false;
                                    details.isEnabled = false;
                                    sender.isEnabled = false;
                                    sendBtn.visibility= View.GONE
                                    deleteBtn.visibility= View.GONE

                                    deletePost(title1)
                                }
                            }else{
                                Toast.makeText(this, "No Post with that title was found!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    sendBtn.visibility = View.VISIBLE
                    deleteBtn.visibility = View.VISIBLE
                }
            }else{
                binding.managePostUser.isEnabled = false;
                reply.isEnabled = false;
                details.isEnabled = false;

                sendBtn.visibility = View.GONE
                deleteBtn.visibility = View.GONE

                binding.managePostSearch.setOnClickListener {
                    var name = binding.managePostTitle.text.toString().trim()
                    if(name == ""){
                        Toast.makeText(this, "Please enter a Title!", Toast.LENGTH_SHORT).show()
                    }else{
                        postReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Posts")
                        postReference.child(name).get().addOnSuccessListener {
                            if(it.exists()){
                                val title1 = it.child("title").value.toString()
                                val sender1 = it.child("userEmail").value.toString()
                                val details1 = it.child("details").value.toString()
                                val reply = it.child("reply").value.toString()

                                binding.managePostUser.setText(sender1.toString())
                                binding.managePostDetails.setText(details1.toString())
                                binding.managePostReply.setText(reply.toString())

                            }else{
                                Toast.makeText(this, "No Post with that title was found!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }



        }.addOnFailureListener{

        }


    }

    private fun deletePost(title1: String) {
        postReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Posts")
        postReference.child(title1).removeValue().addOnSuccessListener {
            Toast.makeText(this, "The post was successfully deleted!", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Error! Post was not deleted!", Toast.LENGTH_LONG).show()
        }
    }

    private fun replySend(reply: String, title:String) {
        postReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Posts")
        val post = mapOf<String, String>(
            "reply" to reply,
            "title" to title,
        )

        postReference.child(title).updateChildren(post).addOnSuccessListener {
            Toast.makeText(this, "Reply successfully sent!", Toast.LENGTH_LONG).show()
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to send reply!", Toast.LENGTH_LONG).show()
        }
    }

    private fun searchTitle(name: String) {

    }

    private fun returnRole(role: String) {
        this.role = role
    }

    private fun checkifInstructor(uid: String) {

    }

    private fun checkifStudent(uid: String) {

    }
}