package com.example.finalproject

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.ActionBar
import com.example.finalproject.databinding.ActivitySetAppointmentBinding
import com.example.finalproject.databinding.ActivityStudentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.google.protobuf.LazyStringArrayList
import java.util.*

class SetAppointment : AppCompatActivity() {
    public lateinit var actionBar: ActionBar
    public lateinit var binding: ActivitySetAppointmentBinding
    public lateinit var auth: FirebaseAuth
    public lateinit var databaseReference: DatabaseReference
    public lateinit var InstructorsDatabase: DatabaseReference
    public lateinit var studentDatabase : DatabaseReference
    public lateinit var storageReference: StorageReference
    public lateinit var uid:String
    public lateinit var userId:String

    public lateinit var evtTitle: EditText
    public lateinit var venue: EditText
    public lateinit var startTime:EditText
    public lateinit var endTime:EditText
    public lateinit var date:EditText
    public lateinit var recipient:EditText

    public lateinit var setAppointment: Button
    public lateinit var cancelBack: Button
    public lateinit var clearBtn: Button
    public var comp = false

    var startHour = 0
    var endHour = 0
    var startMinute = 0
    var endMinute = 0
    var eventYear = 0
    var eventMonth = 0
    var eventDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar=supportActionBar!!
        actionBar.title="Set Appointment"

        evtTitle = findViewById<EditText>(R.id.inTitle)
        venue = findViewById<EditText>(R.id.inVenue)
        startTime = findViewById<EditText>(R.id.inStart)
        endTime = findViewById<EditText>(R.id.inEnd)
        date = findViewById<EditText>(R.id.inDate)
        recipient = findViewById(R.id.recipientEmail)
        setAppointment = findViewById<Button>(R.id.saveEvt)
        cancelBack=findViewById<Button>(R.id.backBtn)
        clearBtn=findViewById<Button>(R.id.clearBtn)

        startTime.isEnabled = false;
        endTime.isEnabled = false;
        date.isEnabled = false;

        var pickStartTime = findViewById<ImageButton>(R.id.startTimeIcon)
        var pickEndTime = findViewById<ImageButton>(R.id.endTimeIcon)
        var pickDate = findViewById<ImageButton>(R.id.pickDateIcon)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        pickDate.setOnClickListener{
            val date = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
                date.setText(""+dayOfMonth+"/"+month+"/"+year)
                eventYear = year
                eventMonth = monthOfYear
                eventDay = dayOfMonth
            },year, month, day)
            date.show()
        }

        pickStartTime.setOnClickListener{
            var pickStartTime: TimePickerDialog
            var mcurrentTime = Calendar.getInstance()
            var hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            var minute = mcurrentTime.get(Calendar.MINUTE)

            pickStartTime = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    var ampm:String
                    var hr = hourOfDay
                    when{hourOfDay == 0 ->{
                        hr +=12
                        ampm = "AM"
                    }
                        hourOfDay == 12 -> ampm = "PM"
                        hourOfDay > 12 ->{
                            hr -= 12
                            ampm = "PM"
                        }
                        else-> ampm = "AM"
                    }
                    if(startTime != null){
                        var finhour = ""
                        if(hr < 10){
                            finhour = "0$hr"
                        } else{
                            finhour = "$hr"
                        }
                        var min = if (minute < 10) "0$minute" else minute
                        startHour = hr
                        startMinute = minute

                        var msg = "$finhour : $min $ampm"
                        startTime.setText(String.format(msg))
                    }
                }

            }, hour, minute, false)
                pickStartTime.show()
        }

        pickEndTime.setOnClickListener{
            var pickEndTime: TimePickerDialog
            var mcurrentTime = Calendar.getInstance()
            var hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            var minute = mcurrentTime.get(Calendar.MINUTE)

            pickEndTime = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    var ampm:String
                    var hr = hourOfDay
                    when{hourOfDay == 0 ->{
                        hr +=12
                        ampm = "AM"
                    }
                        hourOfDay == 12 -> ampm = "PM"
                        hourOfDay > 12 ->{
                            hr -= 12
                            ampm = "PM"
                        }
                        else-> ampm = "AM"
                    }
                    if(endTime != null){
                        var finhour = ""
                        if(hr < 10){
                            finhour = "0$hr"
                        } else{
                            finhour = "$hr"
                        }
                        var min = if (minute < 10) "0$minute" else minute
                        startHour = hr
                        startMinute = minute

                        var msg = "$finhour : $min $ampm"
                        endTime.setText(String.format(msg))
                    }
                }

            }, hour, minute, false)
            pickEndTime.show()
        }



        auth= FirebaseAuth.getInstance()
        val firebaseUser=auth.currentUser
        uid=auth.currentUser!!.uid
        databaseReference= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Appointments")
        InstructorsDatabase= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Instructors")

        studentDatabase= FirebaseDatabase.getInstance("https://finalproject-7a07c-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Students")

        uid=auth.currentUser!!.uid

        studentDatabase.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                var userId2 = it.child("userId").value
                userId = userId2.toString()
            }
        }

        setAppointment?.setOnClickListener{
            var inEvent = binding.inTitle.text.toString().trim()
            var inVenue = binding.inVenue.text.toString().trim()
            var inStart = binding.inStart.text.toString().trim()
            var inEnd = binding.inEnd.text.toString().trim()
            var inDate = binding.inDate.text.toString().trim()
            var inRecipient = binding.recipientEmail.text.toString().trim()

            validate(inEvent, inVenue, inStart, inEnd, inDate, inRecipient)

            if(comp == true){
                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            }else{

                val firebaseUser=auth.currentUser
                val senderEmail=auth.currentUser?.email


                val newAppointment = Appointment(inEvent, inVenue, inStart, inEnd, inDate, inRecipient, "student@gmail.com")
                    if(uid!=null){
                        databaseReference.child(userId).setValue(newAppointment).addOnCompleteListener {
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                //InstructorsDatabase.child(inRecipient).get().addOnSuccessListener {
                    //if(it.exists()){
                        //Toast.makeText(this, "Email Exists", Toast.LENGTH_SHORT).show()
                    //}else{
                      //  Toast.makeText(this, "Email Doesn't Exist", Toast.LENGTH_SHORT).show()
                    //}
                //}
            }
        }

        clearBtn?.setOnClickListener{
            evtTitle.setText("")
            venue.setText("")
            startTime.setText("")
            endTime.setText("")
            date.setText("")
        }

        cancelBack?.setOnClickListener{
            finish()
        }
    }

    private fun validate(inEvent: String, inVenue: String, inStart: String, inEnd: String, inDate: String, inRecipient:String) {
        if(!Patterns.EMAIL_ADDRESS.matcher(inRecipient).matches()){
            binding.recipientEmail.error="Invalid email" //displays in the editTextView
            comp = true
        }else if(TextUtils.isEmpty(inEvent)|| TextUtils.isEmpty(inVenue) || TextUtils.isEmpty(inStart) || TextUtils.isEmpty(inEnd)||TextUtils.isEmpty(inDate)||TextUtils.isEmpty(inRecipient)){
            comp = true
        }else{
            comp = false
        }
    }
}