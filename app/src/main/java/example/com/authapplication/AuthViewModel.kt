package example.com.authapplication

import android.view.View
import androidx.lifecycle.ViewModel

class AuthViewModel: ViewModel() {
//    private val model: AuthModel = AuthModel()
    var dialogEmail:  String = ""
    private var mPassword: String = ""
    var password: String
        set(value) {
            setPassword(value)
        }
        get() = mPassword

    var onChangePassword: ((password: String, showSym: Boolean) -> Unit)? = null
    var onClickButtonFinger: (() -> Unit)? = null
    var onClickButtonRegister: (() -> Unit)? = null
    var onClickButtonRemember: (() -> Unit)? = null
    var onAnyClick: (() -> Unit)? = null

    fun onClick(v: View) {
        onAnyClick?.invoke()
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

    fun correctEmail(email: String) = validateMail(email)
    fun getStringFromResource(id: Int): String = getStringResource(id)
    fun getColorFromResource(id: Int): Int = getColorResource(id)
}