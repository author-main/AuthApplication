package example.com.authapplication.dialogs

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import example.com.authapplication.interfaces.AuthRegistrationUser
import example.com.authapplication.interfaces.AuthRestoreUser

class DialogStore(val context: Context) {
    var onRegistrationUser: AuthRegistrationUser? = null
    var onRestoreUser: AuthRestoreUser? = null
    private val tagRestore  = "DIALOG_RESTORE"
    private val tagRegister = "DIALOG_REGISTRATION"
    private var dialogRestore: DialogRestore? = null
    private var dialogRegister: DialogRegister? = null
    private var dialogProgress: DialogProgress? = null
    private val fragmentManager = (context as FragmentActivity).supportFragmentManager

    fun showDialogRegister(email: String){
        dialogRegister = DialogRegister()
        dialogRegister?.let { dialog ->
            dialog.onRegisterUser = { email: String, password: String ->
                showProgress()
                onRegistrationUser?.onRegistrationUser(email, password)
            }
            dialog.arguments = Bundle().apply {
                putString("email", email)
            }
            dialog.show(fragmentManager, tagRegister)
        }
    }

    fun showDialogRestore(email: String){
        dialogRestore = DialogRestore()
        dialogRestore?.let { dialog ->
            dialog.onRestoreUser = { email: String ->
                showProgress()
                onRestoreUser?.onRestoreUser(email)
            }
            dialog.arguments = Bundle().apply {
                putString("email", email)
            }
            dialog.show(fragmentManager, tagRestore)
        }
    }

    fun showProgress(){
        dialogProgress = DialogProgress(context)
        dialogProgress?.show()
    }

    fun hideProgress(){
        dialogProgress?.dismiss()
    }
}