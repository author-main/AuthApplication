package example.com.authapplication.interfaces

import example.com.authapplication.AuthValue

interface AuthResultListener {
    fun onComplete(value: AuthValue)
}