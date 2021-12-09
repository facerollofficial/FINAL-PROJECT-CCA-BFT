package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PostsList : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_list)

        postRecyclerView = findViewById(R.id.postList)
        postRecyclerView.layoutManager = LinearLayoutManager(this)
        postRecyclerView.setHasFixedSize(true)

        actionBar=supportActionBar!!
        actionBar.title="Posts"
        postArrayList = arrayListOf<Post>()

        getPostsData()

    }

    private fun getPostsData() {

        dbref = FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Posts")

        dbref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(postSnapshot in snapshot.children){
                        val post = postSnapshot.getValue(Post::class.java)
                        postArrayList.add(post!!)
                    }
                }
                var adapter = MyAdapter(postArrayList)
                postRecyclerView.adapter = MyAdapter(postArrayList)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}