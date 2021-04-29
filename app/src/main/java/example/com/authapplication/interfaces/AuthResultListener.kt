package example.com.authapplication.interfaces

import example.com.authapplication.AuthAction
import example.com.authapplication.AuthValue

interface AuthResultListener {
    fun onComplete(action: AuthAction, result: AuthValue)
}