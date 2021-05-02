package example.com.authapplication.interfaces

import javax.crypto.Cipher

interface AuthPasswordStore {
    fun putPassword(password: String)
    fun getPassword(): String?
    fun existPasswordStore(): Boolean
    fun getCryptoObject(): Cipher?
}