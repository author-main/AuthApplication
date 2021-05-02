package example.com.authapplication.interfaces

import example.com.authapplication.data.AuthAction
import example.com.authapplication.data.AuthValue

interface AuthResultListener {
    fun onAutentificationComplete(action: AuthAction, result: AuthValue)
}