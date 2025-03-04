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
            else -> super.onOptionsItemSelected(item)
        }
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
