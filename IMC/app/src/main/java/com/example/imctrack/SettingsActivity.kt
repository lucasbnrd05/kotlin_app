package com.example.imctrack

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var userIdentifierButton: Button
    private lateinit var saveUserButton: Button
    private lateinit var deleteUsersButton: Button
    private lateinit var newUserEditText: EditText
    private lateinit var userIdentifierSpinner: Spinner
    private var usersList = mutableListOf<String>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        // Ensure edge-to-edge mode for full-screen experience
        enableEdgeToEdge()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Trouver la BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_settings)
        bottomNavigationView.visibility = View.VISIBLE
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        setAppropriateTheme()

        // View to apply window insets (system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        userIdentifierButton = findViewById(R.id.userIdentifierButton)
        newUserEditText = findViewById(R.id.newUserEditText)
        saveUserButton = findViewById(R.id.saveUserButton)
        userIdentifierSpinner = findViewById(R.id.userIdentifierSpinner)
        deleteUsersButton = findViewById(R.id.deleteUsersButton)

        userIdentifierButton.setOnClickListener {
            newUserEditText.visibility = View.VISIBLE
            saveUserButton.visibility = View.VISIBLE
        }

        saveUserButton.setOnClickListener {
            val newUserName = newUserEditText.text.toString().trim()
            if (newUserName.isNotEmpty()) {
                sharedPreferencesHelper.addUser(newUserName)
                updateUserSpinner()
                newUserEditText.text.clear()
                newUserEditText.visibility = View.GONE
                saveUserButton.visibility = View.GONE
                Toast.makeText(this, "User saved: $newUserName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid username", Toast.LENGTH_SHORT).show()
            }
        }

        deleteUsersButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Naviguer vers MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.info -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    true


                }
                else -> false
            }
        }




    }
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete all users")
            .setMessage("Are you sure you want to delete all users?")
            .setPositiveButton("Yes") { _, _ ->
                sharedPreferencesHelper.clearUsers()
                updateUserSpinner()
                Toast.makeText(this, "All users deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
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

    private fun updateUserSpinner() {
        val usersSet = sharedPreferencesHelper.getUsers()
        usersList = usersSet.toMutableList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usersList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userIdentifierSpinner.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        updateUserSpinner()
    }


}