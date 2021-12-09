package com.example.finalproject

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.finalproject.databinding.ActivityEmailBinding
import com.example.finalproject.databinding.ActivitySignupBinding

class Email : AppCompatActivity() {

    private lateinit var binding: ActivityEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

//Binding variables with UI elements
        val sendMail = findViewById<Button>(R.id.proceedToGmail)
        val cancelMail = findViewById<Button>(R.id.btnCancelMsg)
        val clearMail = findViewById<Button>(R.id.clearMsg)

        val subj = findViewById<EditText>(R.id.inSubject)
        val msg = findViewById<EditText>(R.id.inMessage)
        val recipient = findViewById<EditText>(R.id.inEmail)
        var iSend: Intent

        sendMail?.setOnClickListener {
            var subject = subj.text.toString()
            var message = msg.text.toString()
            var reciever = recipient.text.toString()
            if(subject==""||message==""||reciever==""){
                Toast.makeText(this, "Fill in all fields!", Toast.LENGTH_SHORT).show()
            }else{
                iSend= Intent(Intent.ACTION_SENDTO).apply{
                    data= Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL,arrayOf(recipient.text.toString()))
                    putExtra(Intent.EXTRA_SUBJECT, subj.text.toString())
                    putExtra(Intent.EXTRA_TEXT, msg.text.toString())}
                try {
                    startActivity(iSend)
                    Toast.makeText(this, "Launching GMAIL!", Toast.LENGTH_LONG).show()
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "Content error", Toast.LENGTH_LONG).show()
                }
            }

        }

        cancelMail?.setOnClickListener{
            finish()
        }

        clearMail?.setOnClickListener{
            subj.setText("")
            msg.setText("")
            recipient.setText("")
        }

    }
}