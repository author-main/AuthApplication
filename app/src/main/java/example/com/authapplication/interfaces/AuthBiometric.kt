package example.com.authapplication.interfaces

import javax.crypto.Cipher

interface AuthBiometric {
    var authBiometricListener: AuthBiometricResultListener?
    fun canAuthenticate () : Boolean
    fun authenticate(cryptoObject: Cipher?): Boolean
}