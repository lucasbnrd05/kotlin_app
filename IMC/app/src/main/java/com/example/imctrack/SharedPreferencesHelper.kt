import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_config_preferences", Context.MODE_PRIVATE)

    // Sauvegarder le thème choisi
    fun saveTheme(isDarkMode: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkMode", isDarkMode)
        editor.apply()
    }

    // Récupérer le thème choisi
    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean("isDarkMode", false) // false = thème clair par défaut
    }
}
