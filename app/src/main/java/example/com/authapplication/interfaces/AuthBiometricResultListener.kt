package example.com.authapplication.interfaces

import example.com.authapplication.AuthBiometricValue

interface AuthBiometricResultListener {
    fun onAuthentificationBiometricComplete(result: AuthBiometricValue)
}