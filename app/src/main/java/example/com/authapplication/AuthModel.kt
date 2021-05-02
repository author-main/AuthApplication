package example.com.authapplication

import android.content.Context
import android.graphics.Color
import android.util.Patterns
import androidx.core.content.ContextCompat
import example.com.authapplication.AuthApplication.Companion.applicationContext
import example.com.authapplication.auth_service.AuthFingerPrint
import example.com.authapplication.auth_service.FirebaseAuthService
import example.com.authapplication.interfaces.AuthBiometric
import example.com.authapplication.interfaces.AuthEmailStore
import example.com.authapplication.interfaces.AuthPasswordStore
import example.com.authapplication.interfaces.AuthService
import example.com.authapplication.store.AuthEncryptPasswordStore
import example.com.authapplication.store.AuthMailStore

class AuthModel(private val context: Context) {
    private val authService      : AuthService        = FirebaseAuthService()
    private val emailAddressStore: AuthEmailStore     = AuthMailStore()
    private val passwordStore    : AuthPasswordStore  = AuthEncryptPasswordStore()
    private val authBiometric    : AuthBiometric      = AuthFingerPrint(context)
    init{

    }
}