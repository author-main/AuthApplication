package example.com.authapplication.stores

import android.content.Context
import android.content.SharedPreferences
import example.com.authapplication.AuthApplication
import example.com.authapplication.interfaces.AuthEmailStore

class AuthMailStore: AuthEmailStore {
    private val filePreferences = "settings"
    private val keyEmail        = "email"
    private val sharedPrefs: SharedPreferences =
            AuthApplication.applicationContext().getSharedPreferences(filePreferences, Context.MODE_PRIVATE)

    override fun putEmail(email: String) {
        sharedPrefs.edit().putString(keyEmail, email).apply()
    }

    override fun getEmail(): String? = sharedPrefs.getString(keyEmail, null)
}
