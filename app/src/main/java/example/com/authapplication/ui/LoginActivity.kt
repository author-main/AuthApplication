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
import example.com.authapplication.message_handler.MessageHandler
import example.com.authapplication.mvvm.AuthViewModel
import kotlinx.coroutines.*
import javax.crypto.Cipher

/**
 *
 * Пример аутентификации пользователя
 * с использованием службы Firebase Authentication.
 * Полный код проекта для AndroidStudio доступен
 * по адресу https://github.com/author-main/AuthApplication.
 * Использование кода разрешено без каких-либо ограничений
 * Все вопросы и пояснения вы можете получить по email, указанному ниже.
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
    private lateinit var dataBinding: ActivityFullscreenBinding
    private lateinit var viewModel: AuthViewModel
    private val symbols = arrayOfNulls<TextView>(5)
    private val hiddenSymbol = "•"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this).get(AuthViewModel::class.java)
        viewModel.setModelContext(this)
        viewModel.onChangePassword = {password: String, showSym: Boolean ->
            changePassword(password, showSym)
        }
        viewModel.onClickButtonRegister = {
            viewModel.showRegisterDialog(dataBinding.editTextEmail.text.toString())
        }
        viewModel.onClickButtonRemember = {
            viewModel.showRestoreDialog(dataBinding.editTextEmail.text.toString())
        }
        viewModel.onClickButtonFinger   = {
            promptFingerPrint()
        }
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
        getDialogStore()
    }

    private fun getDialogStore(){
        viewModel.getDialogStore(this)
        viewModel.onRegistrationUser = { email: String, password: String ->
            registerUser(email, password)
        }
        viewModel.onRestoreUser = { email: String ->
            restoreUser(email)
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
                    viewModel.showProgress()
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

    private fun restoreUser(email: String){
        viewModel.dialogEmail = email
        viewModel.restoreUser(email)
    }

    private fun registerUser(email: String, password: String){
        viewModel.dialogEmail = email
        viewModel.registerUser(email, password)
    }

    private fun authenticationComplete(action: AuthAction, result: AuthValue){
        fun updateEmail(){
            dataBinding.editTextEmail.setText(viewModel.dialogEmail)
            viewModel.saveEmailAddress(viewModel.dialogEmail)
        }
        viewModel.hideProgress()
        if (result != AuthValue.SUCCESSFUL){
            MessageHandler.showError(result, this)
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
                MessageHandler.showMessage(getString(R.string.dlgreg_success), this)
            }
            // * Обработка Restore
            AuthAction.RESTORE -> {
                updateEmail()
                MessageHandler.showMessage(getString(R.string.dlgrest_success), this)
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
            viewModel.showProgress()
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
