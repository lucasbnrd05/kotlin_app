package com.example.imctrack

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

    private val gson = Gson()

    fun saveGoals(goals: List<String>) {
        val json = gson.toJson(goals)
        sharedPreferences.edit().putString("user_goals", json).apply()
    }

    fun getGoals(): List<String> {
        val json = sharedPreferences.getString("user_goals", null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    fun clearAPI() {
        sharedPreferences.edit().remove("API_KEY").apply()
    }

    fun clearGoals() {
        sharedPreferences.edit().remove("user_goals").apply()
    }
}