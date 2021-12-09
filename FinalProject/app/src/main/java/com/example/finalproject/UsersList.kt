package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class UsersList : AppCompatActivity() {
    private lateinit var dbref :DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<Student>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)

        userRecyclerView = findViewById(R.id.userList)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf<Student>()
        getUserData()
    }

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Students")
        dbref.addValueEventListener(object:ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val student = userSnapshot.getValue(Student::class.java)
                        userArrayList.add(student!!)
                    }
                }
                userRecyclerView.adapter = UserAdapter(userArrayList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}