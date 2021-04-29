package example.com.authapplication.interfaces

interface AuthService {
    fun signIn(email: String, password: String)
    fun registerUser(email: String, password: String)
    fun restoreUser(email: String)
    fun verifyUser(email: String, password: String): Boolean
    fun addAuthResult(authResultListener: AuthResultListener)
}