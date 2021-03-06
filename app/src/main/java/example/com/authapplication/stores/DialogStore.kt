package example.com.authapplication.stores

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import example.com.authapplication.AuthApplication
import example.com.authapplication.dialogs.DialogProgress
import example.com.authapplication.dialogs.DialogRegister
import example.com.authapplication.dialogs.DialogRestore
import example.com.authapplication.interfaces.AuthRegistrationUser
import example.com.authapplication.interfaces.AuthRestoreUser

class DialogStore(private var context: Context){
    private val tagDialogRestore  = "DIALOG_RESTORE"
    private val tagDialogRegister = "DIALOG_REGISTER"
    private var dialogRestore : DialogRestore? = null
    private var dialogRegister: DialogRegister? = null
    private var dialogProgress: DialogProgress? = null
    private var mRegistrationUser:   AuthRegistrationUser? = null
    private var mRestoreUser:        AuthRestoreUser? = null


    fun setContext(context: Context){
        this.context = context
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        dialogRestore = fragmentManager.findFragmentByTag(tagDialogRestore) as? DialogRestore
        dialogRestore?.onRestoreUser = { email: String ->
            restoreUser(email)
        }
        dialogRegister = fragmentManager.findFragmentByTag(tagDialogRegister) as? DialogRegister
        dialogRegister?.onRegisterUser = { email: String, password: String ->
            registerUser(email, password)
        }
        if (dialogProgress != null)
            showProgress()
    }

    fun addRegistrationUserListener(value: AuthRegistrationUser){
        mRegistrationUser = value
    }

    fun addRestoreUserListener(value: AuthRestoreUser){
        mRestoreUser = value
    }

    private fun restoreUser(email: String){
        showProgress()
        mRestoreUser?.onRestoreUser(email)
    }

    private fun registerUser(email: String, password: String){
        showProgress()
        mRegistrationUser?.onRegistrationUser(email, password)
    }

    fun showDialogRestore(email: String){
        dialogRestore = DialogRestore()
        dialogRestore?.let { dialog ->
            dialog.onRestoreUser = { email: String ->
                restoreUser(email)
            }
            dialog.arguments = Bundle().apply {
                putString("email", email)
            }
            dialog.show((context as FragmentActivity).supportFragmentManager, tagDialogRestore)
        }
    }

    fun showDialogRegistration(email: String){
        dialogRegister = DialogRegister()
        dialogRegister?.let { dialog ->
            dialog.onRegisterUser = { email: String, password: String ->
                registerUser(email, password)
            }
            dialog.arguments = Bundle().apply {
                putString("email", email)
            }
            dialog.show((context as FragmentActivity).supportFragmentManager, tagDialogRegister)
        }
    }

    fun showProgress(){
        dialogProgress = DialogProgress(context)
        dialogProgress?.show()
    }

    fun hideProgress(){
        dialogProgress?.dismiss()
        dialogProgress = null
    }

  /*  class EasyClass<T, V>(s: T, i: V){
        val name = s
        val old  = i
    }

    val eClass = EasyClass<String, Int>("fuck", 20)*/

}