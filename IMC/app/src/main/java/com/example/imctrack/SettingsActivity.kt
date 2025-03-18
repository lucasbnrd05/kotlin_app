package com.example.imctrack

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var userIdentifierButton: Button
    private lateinit var saveUserButton: Button
    private lateinit var deleteUsersButton: Button
    private lateinit var newUserEditText: EditText
    private lateinit var userIdentifierSpinner: Spinner
    private lateinit var selectUserButton: Button
    private lateinit var editTextApiKey: EditText
    private var usersList = mutableListOf<String>()
    private lateinit var apiKeyTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        enableEdgeToEdge()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_settings)
        bottomNavigationView.visibility = View.VISIBLE
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        setAppropriateTheme()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialisation des vues
        userIdentifierButton = findViewById(R.id.userIdentifierButton)
        editTextApiKey = findViewById(R.id.editTextApiKey)
        newUserEditText = findViewById(R.id.newUserEditText)
        saveUserButton = findViewById(R.id.saveUserButton)
        userIdentifierSpinner = findViewById(R.id.userIdentifierSpinner)
        deleteUsersButton = findViewById(R.id.deleteUsersButton)
        selectUserButton = findViewById(R.id.selectUserButton)
        apiKeyTextView = findViewById(R.id.apiKeyTextView) // Initialise le TextView
        updateApiKeyTextView()

        // Configuration initiale de la visibilité
        newUserEditText.visibility = View.GONE
        saveUserButton.visibility = View.GONE
        selectUserButton.visibility = if (sharedPreferencesHelper.getUsers().isNotEmpty()) View.VISIBLE else View.GONE

        // Mise à jour du Spinner avec les utilisateurs existants
        updateUserSpinner()


        // Gestion du clic sur le bouton "Enter User ID"
        userIdentifierButton.setOnClickListener {
            newUserEditText.visibility = View.VISIBLE
            saveUserButton.visibility = View.VISIBLE
            selectUserButton.visibility = View.GONE
        }

        // Gestion du clic sur le bouton "Save User"
        saveUserButton.setOnClickListener {
            val newUserName = newUserEditText.text.toString().trim()
            val newApiKey = editTextApiKey.text.toString()

            if (newUserName.isNotEmpty() && newApiKey.isNotEmpty()) {
                sharedPreferencesHelper.addUser(newUserName)
                updateUserSpinner()

                newUserEditText.text.clear()
                newUserEditText.visibility = View.GONE
                saveUserButton.visibility = View.GONE
                selectUserButton.visibility = View.VISIBLE

                val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                sharedPreferences.edit().apply {
                    putString("userIdentifier", newUserName)
                    putString("API_KEY", newApiKey)
                }.apply()

                Toast.makeText(this, "User saved: $newUserName", Toast.LENGTH_SHORT).show()
                updateApiKeyTextView() // Met à jour l'affichage de la clé API
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a valid username and API key", Toast.LENGTH_SHORT).show()
            }
        }

        // Gestion du clic sur le bouton "DELETE"
        deleteUsersButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Gestion du clic sur le bouton "Select User"
        selectUserButton.setOnClickListener {
            val selectedUser = userIdentifierSpinner.selectedItem.toString()
            val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("userIdentifier", selectedUser).apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Gestion du Spinner
        userIdentifierSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedUser = parent?.getItemAtPosition(position).toString()
                val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("userIdentifier", selectedUser).apply()
                Toast.makeText(this@SettingsActivity, "Selected user: $selectedUser", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Ne rien faire
            }
        }

        // Gestion de la BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
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

    // Fonction pour afficher une boîte de dialogue de confirmation de suppression
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete all users")
            .setMessage("Are you sure you want to delete all users?")
            .setPositiveButton("Yes") { _, _ ->
                sharedPreferencesHelper.clearUsers()
                sharedPreferencesHelper.clearAPI()
                val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("API_KEY", "No Api key").apply()

                // Ajoute ceci :
                resetDatabase()
                apiKeyTextView.text = "No Api key" // Vider le textview

                editTextApiKey.clearFocus() //Retirer la focus de l'edittext
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editTextApiKey.windowToken, 0)

                updateUserSpinner()
                selectUserButton.visibility = View.GONE
                Toast.makeText(this, "All users deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Met à jour le TextView avec la clé API (ou un message vide)
    private fun updateApiKeyTextView() {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val apiKey = sharedPreferences.getString("API_KEY", "")
        apiKeyTextView.text = if (!apiKey.isNullOrEmpty())  "Clé API: $apiKey" else "No Api key"
    }
    // Ajoute cette fonction à SettingsActivity :
    private fun resetDatabase() {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDatabaseInitialized", false)
        editor.apply()
    }


    // Fonction pour mettre à jour le Spinner avec les utilisateurs existants
    private fun updateUserSpinner() {
        val usersSet = sharedPreferencesHelper.getUsers()
        usersList = usersSet.toMutableList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usersList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userIdentifierSpinner.adapter = adapter
        selectUserButton.visibility = if (usersList.isNotEmpty()) View.VISIBLE else View.GONE
    }

    // Fonction pour définir le thème approprié
    private fun setAppropriateTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    // Fonction pour basculer entre les thèmes
    private fun toggleTheme() {
        val isDarkMode = sharedPreferencesHelper.getTheme()
        sharedPreferencesHelper.saveTheme(!isDarkMode)
        setAppropriateTheme()
    }

    // Gestion du menu d'options
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val themeItem = menu?.findItem(R.id.action_theme)
        val isDarkMode = sharedPreferencesHelper.getTheme()

        themeItem?.setIcon(
            if (isDarkMode) R.drawable.ic_sun else R.drawable.ic_moon
        )

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        updateUserSpinner()
        updateApiKeyTextView()
    }
}