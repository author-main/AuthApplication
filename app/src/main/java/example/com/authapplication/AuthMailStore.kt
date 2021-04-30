package example.com.authapplication

import android.content.Context
import android.content.SharedPreferences
import example.com.authapplication.interfaces.AuthEmailStore

class AuthMailStore(): AuthEmailStore {
    private val filePreferences = "settings"
    private val keyEmail        = "email"
    override fun putEmail(email: String) {
        val sharedPrefs: SharedPreferences =
            AuthApplication.applicationContext().getSharedPreferences(filePreferences, Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(keyEmail, email).apply()
    }

    override fun getEmail(): String? {
        val sharedPrefs: SharedPreferences =
            AuthApplication.applicationContext().getSharedPreferences(filePreferences, Context.MODE_PRIVATE)
        return sharedPrefs.getString(keyEmail, null)
    }
}
