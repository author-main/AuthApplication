package example.com.authapplication.interfaces

import example.com.authapplication.data.AuthBiometricValue

interface AuthBiometricResultListener {
    fun onAuthentificationBiometricComplete(result: AuthBiometricValue)
}