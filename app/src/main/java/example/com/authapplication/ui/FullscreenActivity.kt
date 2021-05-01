package example.com.authapplication.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import example.com.authapplication.*
import example.com.authapplication.auth_service.FirebaseAuthService
import example.com.authapplication.databinding.ActivityFullscreenBinding
import example.com.authapplication.dialogs.DialogProgress
import example.com.authapplication.dialogs.DialogRegister
import example.com.authapplication.interfaces.AuthEmailStore
import example.com.authapplication.interfaces.AuthPasswordStore
import example.com.authapplication.interfaces.AuthResultListener
import example.com.authapplication.interfaces.AuthService
import example.com.authapplication.store.AuthEncryptPasswordStore
import example.com.authapplication.store.AuthMailStore
import kotlinx.coroutines.*


class FullscreenActivity : AppCompatActivity(), AuthResultListener {

    companion object{
        private fun setNightMode() {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            )
        }
    }

    private var job: Job? = null
    private var dialogProgress: DialogProgress? = null
    private var authService: AuthService? = null
    private var emailAddressStore: AuthEmailStore? = null
    private var passwordStore: AuthPasswordStore? = null

    private lateinit var dataBinding: ActivityFullscreenBinding
    private lateinit var viewModel: AuthViewModel
    private val symbols = arrayOfNulls<TextView>(5)


    private fun addAuthService(){
        val authFireBase = FirebaseAuthService()
        authFireBase.authResultListener = this
        this.authService = authFireBase
    }

    private fun addEmailAddressStore(){
        this.emailAddressStore = AuthMailStore()
    }

    private fun addPasswordStore(){
        this.passwordStore = AuthEncryptPasswordStore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val firstStart = !(::viewModel.isInitialized)

        viewModel =
                ViewModelProvider(this).get(AuthViewModel::class.java)

        viewModel.onAnyClick = {hideFocusEmail()}

        viewModel.onChangePassword =
                { password: String, showSym: Boolean -> changePassword(password, showSym) }
        viewModel.onClickButtonRegister = {showDialogRegister()}
        viewModel.onClickButtonRemember = {showDialogRestore()}


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
        addAuthService()
        addEmailAddressStore()
        addPasswordStore()
        dataBinding.editTextEmail.setText(emailAddressStore?.getEmail())
    }

    private fun changePassword(password: String, showSym: Boolean){
        fun hideSym(index: Int){
            symbols[index]?.setTextColor(viewModel.getColorFromResource(
                    R.color.design_default_color_on_primary
            ))
            symbols[index]?.text = "\u2022"
        }
        if (showSym){
            val index = password.length - 1
            val sym = password[index].toString()
            if (index > 0)
                hideSym(index - 1)
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch{
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

    private fun showDialogRestore(){

    }

    private fun showDialogRegister(){
        val dialogRegister = DialogRegister()
        dialogRegister.onRegisterUser = { email: String, password: String ->
            showProgress()
            emailAddressStore?.putEmail(email)
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


    override fun onComplete(action: AuthAction, result: AuthValue){
        hideProgress()
        if (result != AuthValue.SUCCESSFUL){
            showError(result)
            return
        }
        when (action) {
        // * Handling signin
            AuthAction.SIGNIN -> {
                emailAddressStore?.putEmail(dataBinding.editTextEmail.text.toString())
                passwordStore?.putPassword(viewModel.password)
                //startActivity(Intent(this, MainActivity::class.java))
                //  finish()
            }
        // * Handling registration
            AuthAction.REGISTER -> {
               val email = emailAddressStore?.getEmail()
               if (!email.isNullOrEmpty())
               dataBinding.editTextEmail.setText(email)
            }
        // * Handling restore
            AuthAction.RESTORE -> {

            }
        }
    }


    private fun showError(error: AuthValue){
        val errorMessage =
            when (error){
                AuthValue.ERROR_CONNECTION -> {
                    getStringResource(R.string.error_connected_internet)
                }
                AuthValue.ERROR_ALREADY_EMAIL -> {
                    getStringResource(R.string.error_already_email)
                }
                AuthValue.ERROR_USER_DATA -> {
                    getStringResource(R.string.error_login_message)
                }
                AuthValue.ERROR_RESTORE -> {
                    getStringResource(R.string.error_restore_password)
                }
                else -> {
                //AuthValue.ERROR_AUTH_SERVICE ->{
                    getStringResource(R.string.error_auth_service)
                }

            }
        val toast: Toast = Toast.makeText(this, errorMessage, Toast.LENGTH_LONG)
        val centeredText: Spannable = SpannableString(errorMessage)
        centeredText.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0, errorMessage.length - 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        toast.show()
    }

}