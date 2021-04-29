package example.com.authapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import example.com.authapplication.auth_service.FirebaseAuthService
import example.com.authapplication.AuthValue
import example.com.authapplication.AuthViewModel
import example.com.authapplication.interfaces.AuthResultListener
import example.com.authapplication.interfaces.AuthService
import example.com.authapplication.R
import example.com.authapplication.databinding.ActivityFullscreenBinding
import example.com.authapplication.dialogs.DialogProgress
import example.com.authapplication.dialogs.DialogRegister
import example.com.authapplication.log
import kotlinx.coroutines.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    companion object{
        private fun setNightMode() {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            )
        }
    }

    private var dialogProgress: DialogProgress? = null
    private var authService: AuthService? = null

    private lateinit var dataBinding: ActivityFullscreenBinding
    private lateinit var viewModel: AuthViewModel
    private val symbols = arrayOfNulls<TextView>(5)


    private fun addAuthService(authService: AuthService){
        this.authService = authService
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
                ViewModelProvider(this).get(AuthViewModel::class.java)

        viewModel.onAnyClick = {hideFocusEmail()}

        viewModel.onChangePassword =
                { password: String, showSym: Boolean -> changePassword(password, showSym) }
        viewModel.onClickButtonRegister = {showDialogRegister()}


        dataBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_fullscreen
        )
        symbols[0] = dataBinding.textViewSymbol0
        symbols[1] = dataBinding.textViewSymbol1
        symbols[2] = dataBinding.textViewSymbol2
        symbols[3] = dataBinding.textViewSymbol3
        symbols[4] = dataBinding.textViewSymbol4
        dataBinding.eventhandler = viewModel
        supportActionBar?.hide()
        setNightMode()
        val authFireBase = FirebaseAuthService()
        authFireBase.addAuthResult(object: AuthResultListener {
            override fun onComplete(value: AuthValue) {
                authComplete(value)
            }
        })
        addAuthService(authFireBase)
    }

    private fun changePassword(password: String, showSym: Boolean){
        if (showSym){
            val index = password.length - 1
            val sym = password[index].toString()
            CoroutineScope(Dispatchers.Main).launch{
                symbols[index]?.setTextColor(viewModel.getColorFromResource(
                        R.color.design_default_color_on_primary
                ))
                symbols[index]?.text = sym
                delay(400)
                symbols[index]?.text = "\u2022"
                val email = dataBinding.editTextEmail.text.toString()
                if (password.length == 5 && isCorrectEmail(email))
                    authService?.signIn(email, password)

            }
        } else {
            var color = viewModel.getColorFromResource(
                    R.color.design_default_color_on_secondary
            )
            symbols.forEach { textView ->
                textView?.setTextColor(color)
            }

            if (password.isNotEmpty()) {
                color = viewModel.getColorFromResource(R.color.design_default_color_on_primary)
                val count = if (password.length > 5)
                    5
                else
                    password.length
                for (i in 0 until count){
                    symbols[i]?.setTextColor(color)
                }
            }


        }
    }

    private fun hideFocusEmail(){
        if (dataBinding.editTextEmail.isFocused) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(dataBinding.editTextEmail.windowToken, 0)
            dataBinding.editTextEmail.isFocusableInTouchMode = false
            dataBinding.editTextEmail.clearFocus()
            dataBinding.editTextEmail.isFocusableInTouchMode = true
        }
    }

    private fun authComplete(result: AuthValue){
        when (result){
            AuthValue.SUCCESSFUL -> {
                log("signin")
            }
            AuthValue.ERROR_USER_DATA -> {
                log("error signin")
            }
        }
      //  Log.i("Info", "ok")
    }

    private fun isCorrectEmail(email: String): Boolean{
        val result = viewModel.correctEmail(email)
        val editTextMail = dataBinding.editTextEmail
        if (!result) {
            viewModel.password = ""
            editTextMail.error = viewModel.getStringFromResource(R.string.incorrect_email)
            editTextMail.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editTextMail, 0)
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        else
            editTextMail.error = null
        return result
    }

    private fun showDialogRegister(){
        val dialogRegister = DialogRegister()
        dialogRegister.onRegisterUser = { email: String, password: String ->
            showProgress()
            authService?.registerUser(email, password)
        }
        dialogRegister.arguments = Bundle().apply {
            putString("email", dataBinding.editTextEmail.text.toString())
        }
        dialogRegister.isCancelable = false
        dialogRegister.show(supportFragmentManager, "DIALOG_REGISTER")
    }


    private fun showProgress() {
        dialogProgress = DialogProgress(this)
        dialogProgress?.show()
    }

    private fun hideProgress() {
        dialogProgress?.dismiss()
    }

}