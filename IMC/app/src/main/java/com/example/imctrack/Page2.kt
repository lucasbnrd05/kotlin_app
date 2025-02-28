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
