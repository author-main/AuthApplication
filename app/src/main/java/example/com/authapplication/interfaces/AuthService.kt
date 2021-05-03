package example.com.authapplication.interfaces

interface AuthService {
    var authResultListener: AuthResultListener?
    fun signIn(email: String, password: String)
    fun registerUser(email: String, password: String)
    fun restoreUser(email: String)
    fun getUidUser(): String?
}