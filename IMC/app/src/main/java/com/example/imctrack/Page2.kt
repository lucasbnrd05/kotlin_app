package com.example.imctrack

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.example.imctrack.MainActivity
import com.example.imctrack.Page3
import com.example.imctrack.R
import com.example.imctrack.SharedPreferencesHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Page2 : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page2)
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        setAppropriateTheme()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Views
        val heightEditText: EditText = findViewById(R.id.editHeight)
        val weightEditText: EditText = findViewById(R.id.editWeight)
        val calculateButton: Button = findViewById(R.id.calculateBMI)
        val resultTextView: TextView = findViewById(R.id.resultBMI)
        val adviceTextView: TextView = findViewById(R.id.adviceTextView)

        // Button click to calculate BMI
        calculateButton.setOnClickListener {
            // Get the height and weight values from the user
            val heightStr = heightEditText.text.toString()
            val weightStr = weightEditText.text.toString()

            // Validate inputs
            if (heightStr.isEmpty() || weightStr.isEmpty()) {
                Toast.makeText(this, "Please enter both height and weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert height and weight to float
            val height = heightStr.toFloat()
            val weight = weightStr.toFloat()

            // Calculate BMI (height is in cm, so convert to meters)
            val heightInMeters = height / 100
            val bmi = weight / (heightInMeters * heightInMeters)

            ////////////////////////////////////////
            val sharedPrefs = getSharedPreferences("IMC_TRACKER", MODE_PRIVATE)
            val editor = sharedPrefs.edit()

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            val existingHeights = sharedPrefs.getString("HEIGHT_LIST", "") ?: ""
            val existingWeights = sharedPrefs.getString("WEIGHT_LIST", "") ?: ""
            val existingBmi = sharedPrefs.getString("BMI_LIST", "") ?: ""
            val existingDates = sharedPrefs.getString("DATE_LIST", "") ?: ""

            val updatedHeights = if (existingHeights.isEmpty()) "$height" else "$existingHeights,$height"
            val updatedWeights = if (existingWeights.isEmpty()) "$weight" else "$existingWeights,$weight"
            val updatedBmi = if (existingBmi.isEmpty()) "$bmi" else "$existingBmi,$bmi"
            val updatedDates = if (existingDates.isEmpty()) currentDate else "$existingDates,$currentDate"

            editor.putString("HEIGHT_LIST", updatedHeights)
            editor.putString("WEIGHT_LIST", updatedWeights)
            editor.putString("BMI_LIST", updatedBmi)
            editor.putString("DATE_LIST", updatedDates)
            editor.apply()
            /////////////////////////////////////////

            // Display BMI result
            resultTextView.text = "Your BMI is: %.2f".format(bmi)

            // Provide advice based on BMI result
            val advice = when {
                bmi < 18.5 -> "Underweight. It's recommended to consult a doctor to improve your nutrition."
                bmi in 18.5..24.9 -> "Normal weight. Keep maintaining a healthy lifestyle with balanced eating and exercise."
                bmi in 25.0..29.9 -> "Overweight. Try adopting a more balanced diet and regular physical activity."
                bmi in 30.0..34.9 -> "Moderate obesity. It's important to consult a healthcare professional to create a plan for action."
                bmi in 35.0..39.9 -> "Severe obesity. Medical follow-up is strongly recommended for weight management."
                else -> "Morbid or massive obesity. It's crucial to consult a doctor to assess treatment options."
            }

            // Display the advice
            adviceTextView.text = advice
        }


        // Bottom Navigation
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_previous -> {
                    // Go to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_next -> {
                    // Go to Page3
                    val intent = Intent(this, Page3::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                toggleTheme()
                true
            }

            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.action_logout -> {
                logoutUser()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    private fun setAppropriateTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun toggleTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        sharedPreferencesHelper.saveTheme(!isDarkMode)
        setAppropriateTheme()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val themeItem = menu?.findItem(R.id.action_theme)
        val isDarkMode = sharedPreferencesHelper.getTheme()

        themeItem?.setIcon(
            if (isDarkMode) R.drawable.ic_sun else R.drawable.ic_moon
        )

        return super.onPrepareOptionsMenu(menu)
    }
}
