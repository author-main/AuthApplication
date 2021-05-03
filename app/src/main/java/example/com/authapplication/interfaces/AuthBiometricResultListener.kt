package example.com.authapplication.interfaces

import javax.crypto.Cipher

interface AuthBiometricResultListener {
    fun onAuthentificationBiometricComplete(cryptoObject: Cipher?)
}