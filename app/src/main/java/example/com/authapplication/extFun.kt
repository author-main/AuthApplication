package example.com.authapplication

import android.graphics.Color
import android.util.Log
import android.util.Patterns
import androidx.core.content.ContextCompat
import java.util.*

fun validateMail(email: String): Boolean {
    return !(email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
}

fun getStringResource(id: Int): String =
        try {
            AuthApplication.applicationContext().getString(id)
        }
        catch (e: Exception){
            ""
        }

fun getColorResource(id: Int): Int =
        try {
            ContextCompat.getColor(
                    AuthApplication.applicationContext(),
                    id
            )
        }
        catch (e: Exception){
            Color.TRANSPARENT
        }

fun log(value: String){
    Log.v("authapp", value.toUpperCase(Locale.ROOT))
}