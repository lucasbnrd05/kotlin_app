package com.example.imctrack

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Ensure edge-to-edge mode for full-screen experience
        enableEdgeToEdge()

        // View to apply window insets (system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI elements
        val editTextUserIdentifier: EditText = findViewById(R.id.editTextUserIdentifier)
        val buttonSave: Button = findViewById(R.id.buttonSave)

        // Load existing user identifier if available
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val userIdentifier = sharedPreferences.getString("userIdentifier", "")
        editTextUserIdentifier.setText(userIdentifier)

        // Save new user identifier on button click
        buttonSave.setOnClickListener {
            val newUserIdentifier = editTextUserIdentifier.text.toString()
            if (newUserIdentifier.isNotBlank()) {
                sharedPreferences.edit().apply {
                    putString("userIdentifier", newUserIdentifier)
                    apply()
                }
                Toast.makeText(this, "User ID saved: $newUserIdentifier", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "User ID cannot be blank", Toast.LENGTH_LONG).show()
            }
        }
    }
}