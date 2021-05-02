package example.com.authapplication.interfaces

import example.com.authapplication.AuthAction
import example.com.authapplication.AuthValue

interface AuthResultListener {
    fun onAutentificationComplete(action: AuthAction, result: AuthValue)
}