package example.com.authapplication.auth_service

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import example.com.authapplication.R
import example.com.authapplication.getStringResource
import example.com.authapplication.interfaces.AuthBiometric
import example.com.authapplication.interfaces.AuthBiometricResultListener
import javax.crypto.Cipher


class AuthFingerPrint(private val context: Context): AuthBiometric {
    override var authBiometricListener: AuthBiometricResultListener? = null


    override fun canAuthenticate() =
            BiometricManager.from(context)
                    .canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS

    override fun authenticate(cryptoObject: Cipher?): Boolean {
        if (!canAuthenticate() || cryptoObject == null)
            return false
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getStringResource(R.string.biometric_title))
                .setConfirmationRequired(false)
                .setNegativeButtonText(getStringResource(R.string.button_cancel))
                .build()


        val biometricPrompt = createBiometricPrompt()
        biometricPrompt.authenticate(
                promptInfo,
                BiometricPrompt.CryptoObject(cryptoObject)
        )
        return true
    }


    private fun createBiometricPrompt(): BiometricPrompt{
        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                authBiometricListener?.onAuthentificationBiometricComplete(null)
            }
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // здесь можно извлечь cryptoObject
                authBiometricListener?.onAuthentificationBiometricComplete(result.cryptoObject?.cipher)
            }
        }
        return BiometricPrompt(context as AppCompatActivity, executor, callback)
    }
}