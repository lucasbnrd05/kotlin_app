package com.example.imctrack

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_config_preferences", Context.MODE_PRIVATE)

    // Sauvegarder le thème choisi
    fun saveTheme(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply()
    }

    // Récupérer le thème choisi
    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean("isDarkMode", false) // false = thème clair par défaut
    }

    // Ajouter un utilisateur à la liste
    fun addUser(userName: String) {
        val usersSet = getUsers().toMutableSet()
        usersSet.add(userName)
        sharedPreferences.edit().putStringSet("user_list", usersSet).apply()
    }

    // Récupérer la liste des utilisateurs
    fun getUsers(): Set<String> {
        return sharedPreferences.getStringSet("user_list", emptySet()) ?: emptySet()
    }

    fun clearUsers() {
        sharedPreferences.edit().remove("user_list").apply()
    }
}