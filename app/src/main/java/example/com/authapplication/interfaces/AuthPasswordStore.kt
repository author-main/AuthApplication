package example.com.authapplication.interfaces

interface AuthPasswordStore {
    fun putPassword(password: String)
    fun getPassword(): String?
    fun existPasswordStore(): Boolean
}