package example.com.authapplication

import android.app.Application
import android.content.Context

class AuthApplication: Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: AuthApplication? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}