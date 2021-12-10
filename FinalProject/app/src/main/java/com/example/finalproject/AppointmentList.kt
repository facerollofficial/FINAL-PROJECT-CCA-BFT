package com.example.finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class AppointmentList : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var appointmentArrayList : ArrayList<Appointment>
    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_list)

        setContentView(R.layout.activity_posts_list)

        postRecyclerView = findViewById(R.id.postList)
        postRecyclerView.layoutManager = LinearLayoutManager(this)
        postRecyclerView.setHasFixedSize(true)

        actionBar=supportActionBar!!
        actionBar.title="Appointments"

        appointmentArrayList = arrayListOf<Appointment>()

        getAppointmentsData()

    }

    private fun getAppointmentsData() {

        dbref = FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Appointments")

        dbref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(postSnapshot in snapshot.children){
                        val appointment = postSnapshot.getValue(Appointment::class.java)
                        appointmentArrayList.add(appointment!!)
                    }
                }
                var adapter = AppointmentAdapter(appointmentArrayList)
                postRecyclerView.adapter = AppointmentAdapter(appointmentArrayList)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}