package example.com.authapplication.ui

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import example.com.authapplication.*
import example.com.authapplication.data.AuthAction
import example.com.authapplication.data.AuthValue
import example.com.authapplication.databinding.ActivityFullscreenBinding
import example.com.authapplication.dialogs.DialogProgress
import example.com.authapplication.dialogs.DialogRegister
import example.com.authapplication.dialogs.DialogRestore
import example.com.authapplication.mvvm.AuthViewModel
import kotlinx.coroutines.*
import javax.crypto.Cipher

/**
 *
 * Пример аутентификации пользователя
 * с использованием службы Firebase Authentication
 * Полный код проекта для AndroidStudio доступен
 * по адресу https://github.com/author-main/AuthApplication
 * Использование кода разрешено без каких-либо ограничений
 * Все вопросы и пояснения вы можете получить по email, указанному ниже
 *
 * Автор: Мышанский Сергей
 * Email: myshansky@yandex.ru
 *
*/

class LoginActivity : AppCompatActivity() {
    companion object{
        private fun setNightMode() {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            )
        }
    }
    private var job: Job? = null
    private var dialogProgress: DialogProgress? = null
    private lateinit var dataBinding: ActivityFullscreenBinding
    private lateinit var viewModel: AuthViewModel
    private val symbols = arrayOfNulls<TextView>(5)
    private val hiddenSymbol = "\u2022"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
                ViewModelProvider(this).get(AuthViewModel::class.java)
        viewModel.setModelContext(this)
        viewModel.onChangePassword =
                { password: String, showSym: Boolean -> changePassword(password, showSym) }
        viewModel.onClickButtonRegister = {showDialogRegister()}
        viewModel.onClickButtonRemember = {showDialogRestore()}
        viewModel.onClickButtonFinger   = {promptFingerPrint()}
        viewModel.onAuthenticationComplete = { action: AuthAction, result: AuthValue ->
            authenticationComplete(action, result)
        }
        viewModel.onAuthenticationBiometricComplete = { cryptoObject: Cipher? ->
            authenticationBiometricComplete(cryptoObject)
        }
        lifecycle.addObserver(viewModel)
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
        dataBinding.editTextEmail.setText(viewModel.loadEmailAddress())
        val biometricAvailable = viewModel.canAuthenticateBiometric()
        val passwordSaved = viewModel.isStoredPassword()
        if (biometricAvailable && passwordSaved) {
            dataBinding.buttonFinger.isEnabled = true
            dataBinding.buttonFinger.alpha = 0.7f
            if (viewModel.promptBiometricVisible)
                promptFingerPrint()
        }

    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val editTextRect = Rect()
        dataBinding.editTextEmail.getGlobalVisibleRect(editTextRect)
        ev?.let { event ->
            if (!editTextRect.contains(event.x.toInt(), event.y.toInt()))
                hideFocusEmail()
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun promptFingerPrint(){
        viewModel.promptBiometricVisible = viewModel.authenticateBiomeric()
    }

    private fun changePassword(password: String, showSym: Boolean = false){
        fun hideSym(index: Int){
            symbols[index]?.setTextColor(viewModel.getColorFromResource(
                    R.color.design_default_color_on_primary
            ))
            symbols[index]?.text = hiddenSymbol
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
                symbols[index]?.text = hiddenSymbol
                val email = dataBinding.editTextEmail.text.toString()
                if (password.length == 5 && isCorrectEmail(email)) {
                    showProgress()
                    viewModel.signIn(email, password)
                }
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
        }
        else
            editTextMail.error = null
        return result
    }

    private fun showDialogRestore(){
        val dialogRestore = DialogRestore()
        dialogRestore.onRestoreUser = { email: String ->
            showProgress()
            viewModel.dialogEmail = email
            viewModel.restoreUser(email)
        }
        dialogRestore.arguments = Bundle().apply {
            putString("email", dataBinding.editTextEmail.text.toString())
        }
        dialogRestore.show(supportFragmentManager, "DIALOG_RESTORE")
    }

    private fun showDialogRegister(){
        val dialogRegister = DialogRegister()
        dialogRegister.onRegisterUser = { email: String, password: String ->
            showProgress()
            viewModel.dialogEmail = email
            viewModel.registerUser(email, password)
        }
        dialogRegister.arguments = Bundle().apply {
            putString("email", dataBinding.editTextEmail.text.toString())
        }
        dialogRegister.show(supportFragmentManager, "DIALOG_REGISTER")
    }


    private fun showProgress() {
        dialogProgress = DialogProgress(this)
        dialogProgress?.show()
    }

    private fun hideProgress() {
        dialogProgress?.dismiss()
    }


    private fun showToast(message: String){
        val toast: Toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        val centeredText: Spannable = SpannableString(message)
        centeredText.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0, message.length - 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        toast.show()
    }

    private fun showError(error: AuthValue){
        val idErrorMessage =
            when (error){
                AuthValue.ERROR_CONNECTION -> {
                    R.string.error_connected_internet
                }
                AuthValue.ERROR_ALREADY_EMAIL -> {
                    R.string.error_already_email
                }
                AuthValue.ERROR_USER_DATA -> {
                    R.string.error_login_message
                }
                AuthValue.ERROR_RESTORE -> {
                    R.string.error_restore_password
                }
                else -> {
                //AuthValue.ERROR_AUTH_SERVICE ->{
                    R.string.error_auth_service
                }

            }
            showToast(getStringResource(idErrorMessage))
    }


    private fun authenticationComplete(action: AuthAction, result: AuthValue){
        fun updateEmail(){
            dataBinding.editTextEmail.setText(viewModel.dialogEmail)
            viewModel.saveEmailAddress(viewModel.dialogEmail)
        }
        hideProgress()
        if (result != AuthValue.SUCCESSFUL){
            showError(result)
            if (action == AuthAction.SIGNIN)
                viewModel.password = ""
            return
        }
        when (action) {
            // * Обработка SignIn
            AuthAction.SIGNIN -> {
                viewModel.saveEmailAddress(dataBinding.editTextEmail.text.toString())
                viewModel.savePassword(viewModel.password)
                accessed()
            }
            // * Обработка Registration
            AuthAction.REGISTER -> {
                updateEmail()
                showToast(getString(R.string.dlgreg_success))
            }
            // * Обработка Restore
            AuthAction.RESTORE -> {
                updateEmail()
                showToast(getString(R.string.dlgrest_success))
            }
        }
    }


    private fun authenticationBiometricComplete(cryptoObject: Cipher?) {
        if (cryptoObject != null) {
            val email    = dataBinding.editTextEmail.text.toString()
            val password = viewModel.loadPassword(cryptoObject)
            if (!isCorrectEmail(email) || password.isNullOrBlank())
                return
            viewModel.password = password
            showProgress()
            viewModel.signIn(email, password)
        }
        else
            viewModel.promptBiometricVisible = false
    }


    private fun accessed(){
    /**
     *
     *  Передаем в главную activity Uid пользователя
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("uiduser", viewModel.getUidUser())
        startActivity(intent)

     *  В главной activity в методе onCreate() получаем Uid пользователя
        val uid = intent.getStringExtra("uiduser")
     *
     */
        finish()
    }

}