package example.com.authapplication.services

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import example.com.authapplication.data.AuthAction
import example.com.authapplication.data.AuthValue
import example.com.authapplication.interfaces.AuthResultListener
import example.com.authapplication.interfaces.AuthService
import java.lang.Exception

class FirebaseAuthService: AuthService {
    private val instance = FirebaseAuth.getInstance()
    override var authResultListener: AuthResultListener? = null


    private fun resultTask(task: Task<AuthResult>, action: AuthAction){
        if (task.isSuccessful)
            authResultListener?.onAutentificationComplete(action, AuthValue.SUCCESSFUL)
        else
            authResultListener?.onAutentificationComplete(action, getErrorFromException(task.exception))
    }

    override fun signIn(email: String, password: String) {
        instance.signInWithEmailAndPassword(email, password+"0").addOnCompleteListener { task ->
            resultTask(task, AuthAction.SIGNIN)
        }
    }

    override fun registerUser(email: String, password: String) {
        instance.createUserWithEmailAndPassword(email, password+"0").addOnCompleteListener {task ->
            resultTask(task, AuthAction.REGISTER)
        }
    }

    override fun restoreUser(email: String) {
        instance.currentUser?.sendEmailVerification()
        instance.sendPasswordResetEmail(email).addOnCompleteListener {task ->
            if (task.isSuccessful)
                authResultListener?.onAutentificationComplete(AuthAction.RESTORE, AuthValue.SUCCESSFUL)
            else
                authResultListener?.onAutentificationComplete(AuthAction.RESTORE, AuthValue.ERROR_RESTORE)
        }
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