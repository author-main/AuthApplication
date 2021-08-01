package example.com.authapplication.stores

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.KEY_ALGORITHM_RSA
import android.util.Base64
import example.com.authapplication.AuthApplication
import example.com.authapplication.interfaces.AuthPasswordStore
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher

class AuthEncryptPasswordStore: AuthPasswordStore {
    private val filePreferences = "settings"
    private val keyPassword     = "password"
    //private val keyCredentials  = "credentials"
    private val sharedPrefs: SharedPreferences =
            AuthApplication.applicationContext().getSharedPreferences(filePreferences, Context.MODE_PRIVATE)
    private val providerKeyStore: String = "AndroidKeyStore"
    private val alias = AuthApplication.applicationContext().packageName
    init{
        if (!initKeys())
            generateKeys()
    }

    private fun initKeys(): Boolean{
        val keyStore = getKeyStore() ?: return false
        return try {
            val privateKey = keyStore.getKey(alias, null)
            val certificate = keyStore.getCertificate(alias)
            privateKey !=null && certificate?.publicKey != null
        } catch (e: Exception){
            false
        }
    }

    private fun generateKeys(){
        if (getKeyStore() == null)
            return
        try {
            val spec: AlgorithmParameterSpec = KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_DECRYPT
            )
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build()
            try {
                val kpGenerator =
                        KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, providerKeyStore)
                kpGenerator.initialize(spec)
                kpGenerator.generateKeyPair()
            } catch (e: Exception){}
        } catch (e: Exception) {}
    }

    private fun getKeyStore(): KeyStore?{
        var keyStore: KeyStore? = null
        return try{
            keyStore = KeyStore.getInstance(providerKeyStore)
            keyStore?.load(null)
            keyStore
        } catch (e: Exception) {
            keyStore?.deleteEntry(alias)
            sharedPrefs.edit().remove(keyPassword).apply()
            //clearCredentials()
            null
        }
    }

    override fun putPassword(password: String) {
        val keyStore = getKeyStore()
        if (keyStore?.getCertificate(alias) == null)
            return
        try {
            val passwordUTF = password.toByteArray(Charsets.UTF_8)
            val encryptPassword = encrypt(
                    keyStore.getCertificate(alias).publicKey,
                    passwordUTF
            )
            encryptPassword?.let {
                sharedPrefs.edit().putString(keyPassword, encryptPassword).apply()
                /*putPreferenceValue(keyPassword, encryptPassword)
                  putPreferenceValue(keyCredentials, true)*/
            }
        } catch (e: Exception){}
    }

    /*private fun clearCredentials() {
        removePreferenceKey(keyCredentials)
        removePreferenceKey(keyPassword)
    }*/

    private fun encrypt(encryptionKey: PublicKey, data: ByteArray): String? {
        return try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey)
            val encrypted = cipher.doFinal(data)
            Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception){
            null
        }
    }


    override fun getPassword(cipher: Cipher): String? {
        return try {
            val encryptedPassword = sharedPrefs.getString(keyPassword, null) ?: return null
            val passwordBase64: ByteArray = Base64.decode(encryptedPassword, Base64.DEFAULT)
            val password = cipher.doFinal(passwordBase64) ?: return null
            String(password)
        } catch(e:java.lang.Exception){
            null
        }
    }

    override fun existPasswordStore() =
        !sharedPrefs.getString(keyPassword, null).isNullOrBlank()
        // sharedPrefs.getBoolean(keyCredentials, false)


    override fun getCryptoObject(): Cipher? {
        val ks = getKeyStore() ?: return null
        return try {
            val privateKey: PrivateKey = ks.getKey(alias, null) as PrivateKey
            val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher
        } catch (e: Exception){
            null
        }
    }


    

    /*private fun <T> putPreferenceValue(key: String, value: T){
      if (value is String)
          sharedPrefs.edit().putString(key, value).apply()
      if (value is Boolean)
          sharedPrefs.edit().putBoolean(key, value).apply()

  }

  private fun removePreferenceKey(key: String){
      sharedPrefs.edit().remove(key).apply()
  }*/

}