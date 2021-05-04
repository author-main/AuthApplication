package example.com.authapplication.mvvm

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import example.com.authapplication.data.AuthAction
import example.com.authapplication.data.AuthValue
import example.com.authapplication.getColorResource
import example.com.authapplication.getStringResource
import example.com.authapplication.validateMail
import javax.crypto.Cipher

class AuthViewModel: ViewModel() {
    private val model: AuthModel = AuthModel()
    var dialogEmail:  String = ""
    var promptBiometricVisible:  Boolean = true
    private var mPassword: String = ""
    var password: String
        set(value) {
            setPassword(value)
        }
        get() = mPassword

    var onAuthenticationComplete:           ((action: AuthAction, result: AuthValue) -> Unit)? = null
    var onAuthenticationBiometricComplete:  ((cryptoObject: Cipher?) -> Unit)? = null
    var onChangePassword: ((password: String, showSym: Boolean) -> Unit)? = null
    var onClickButtonFinger: (() -> Unit)? = null
    var onClickButtonRegister: (() -> Unit)? = null
    var onClickButtonRemember: (() -> Unit)? = null

    fun setModelContext(context: Context){
        model.context = context
        model.onAuthenticationComplete = {action: AuthAction, result: AuthValue ->
            onAuthenticationComplete?.invoke(action, result)
        }
        model.onAuthenticationBiometricComplete = {cryptoObject: Cipher? ->
            onAuthenticationBiometricComplete?.invoke(cryptoObject)
        }
    }

    fun onClick(v: View) {
        val tag = v.tag as String
        if (tag.length == 1) {
            if (mPassword.length < 5) {
                mPassword += tag
                onChangePassword?.invoke(mPassword, true)
            }
        } else {
            when (tag) {
                "finger" -> {
                    onClickButtonFinger?.invoke()
                }
                "delete" -> {
                    if (mPassword.isNotEmpty()) {
                        mPassword = mPassword.dropLast(1)
                        onChangePassword?.invoke(mPassword, false)
                    }
                }
                "register" -> {
                    onClickButtonRegister?.invoke()
                }
                "remember" -> {
                    onClickButtonRemember?.invoke()
                }
            }
        }
    }

    @JvmName("setPassword1")
    private fun setPassword(value: String){
        mPassword = value
        onChangePassword?.invoke(value, false)
    }


    fun saveEmailAddress(value: String){
        model.saveEmailAddress(value)
    }


    fun loadEmailAddress(): String? =
        model.loadEmailAddress()


    fun isStoredPassword() =
        model.isStoredPassword()


    fun savePassword(value: String){
        model.savePassword(value)
    }


    fun loadPassword(cipher: Cipher) =
        model.loadPassword(cipher)


    fun canAuthenticateBiometric() =
        model.canAuthenticateBiometric()


    fun authenticateBiomeric() =
        model.authenticateBiomeric()


    fun signIn(email: String, password: String) {
        model.signIn(email, password)
    }



    fun registerUser(email: String, password: String) {
        model.registerUser(email, password)
    }


    fun restoreUser(email: String) {
        model.restoreUser(email)
    }


    fun getUidUser() =
        model.getUidUser()


    fun correctEmail(email: String) 			= validateMail(email)
    fun getStringFromResource(id: Int): String 	= getStringResource(id)
    fun getColorFromResource(id: Int): Int 		= getColorResource(id)
}