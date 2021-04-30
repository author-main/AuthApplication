package example.com.authapplication.auth_service

import android.util.Log
import com.google.firebase.FirebaseError
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import example.com.authapplication.AuthAction
import example.com.authapplication.AuthValue
import example.com.authapplication.interfaces.AuthResultListener
import example.com.authapplication.interfaces.AuthService
import java.lang.Exception

class FirebaseAuthService: AuthService {
    private val instance = FirebaseAuth.getInstance()
    private var authResultListener: AuthResultListener? = null

    override fun addAuthResult(authResultListener: AuthResultListener) {
        this.authResultListener = authResultListener
    }

    override fun signIn(email: String, password: String) {
        instance.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful)
               authResultListener?.onComplete(AuthAction.SIGNIN, AuthValue.SUCCESSFUL)
            else
               authResultListener?.onComplete(AuthAction.SIGNIN, getErrorFromException(task.exception))
        }
    }

    override fun registerUser(email: String, password: String) {
        instance.createUserWithEmailAndPassword(email, password+"0").addOnCompleteListener {task ->

            if (task.isSuccessful)
                authResultListener?.onComplete(AuthAction.REGISTER, AuthValue.SUCCESSFUL)
            else
                authResultListener?.onComplete(AuthAction.REGISTER, getErrorFromException(task.exception))
        }
    }

    override fun restoreUser(email: String) {
        TODO("Not yet implemented")
    }

    override fun verifyUser(email: String, password: String): Boolean {
        TODO("Not yet implemented")
    }

    private fun getErrorFromException(ex: Exception?): AuthValue{
        ex?.let { exception ->
            if (exception is FirebaseNetworkException)
                return AuthValue.ERROR_CONNECTION
            if (exception is FirebaseAuthUserCollisionException)
                return AuthValue.ERROR_ALREADY_EMAIL
            if (exception is FirebaseAuthException)
                return AuthValue.ERROR_USER_DATA
        }
        return AuthValue.ERROR_AUTH_SERVICE
    }


}