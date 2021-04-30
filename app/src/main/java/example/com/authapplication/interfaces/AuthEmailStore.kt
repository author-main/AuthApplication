package example.com.authapplication.interfaces

interface AuthEmailStore {
    fun putEmail(email: String)
    fun getEmail(): String?
}