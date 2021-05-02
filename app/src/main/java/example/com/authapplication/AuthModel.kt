package example.com.authapplication

import android.content.Context
import example.com.authapplication.auth_service.AuthFingerPrint
import example.com.authapplication.auth_service.FirebaseAuthService
import example.com.authapplication.data.AuthAction
import example.com.authapplication.data.AuthBiometricValue
import example.com.authapplication.data.AuthValue
import example.com.authapplication.interfaces.*
import example.com.authapplication.store.AuthEncryptPasswordStore
import example.com.authapplication.store.AuthMailStore

class AuthModel: AuthResultListener, AuthBiometricResultListener {
    var context: Context? = null
    set(value){
        setContext(value)
    }
    var onAuthenticationComplete:           ((action: AuthAction, result: AuthValue) -> Unit)? = null
    var onAuthenticationBiometricComplete:  ((result: AuthBiometricValue) -> Unit)? = null
    private val authService      : AuthService        = FirebaseAuthService()
    private val emailAddressStore: AuthEmailStore     = AuthMailStore()
    private val passwordStore    : AuthPasswordStore  = AuthEncryptPasswordStore()
    private var authBiometric    : AuthBiometric?     = null

    init{
        authService.authResultListener      = this
    }

    @JvmName("setContext1")
    fun setContext(context: Context?){
        authBiometric = AuthFingerPrint(context!!)
        authBiometric?.authBiometricListener = this
    }

    override fun onAutentificationComplete(action: AuthAction, result: AuthValue) {
        onAuthenticationComplete?.invoke(action, result)
    }

    override fun onAuthentificationBiometricComplete(result: AuthBiometricValue) {
        onAuthenticationBiometricComplete?.invoke(result)
    }

    fun saveEmail(value: String){
        emailAddressStore.putEmail(value)
    }

    fun loadEmail(): String? =
        emailAddressStore.getEmail()

    fun isStoredPassword() = passwordStore.existPasswordStore()

    fun savePassword(value: String){
        passwordStore.putPassword(value)
    }

    fun canAuthenticateBiometric() =
        authBiometric?.canAuthenticate() ?: false

    fun authenticateBiomeric(){
        authBiometric?.authenticate(passwordStore.getCryptoObject())
    }

}