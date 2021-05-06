package example.com.authapplication.dialogs


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import example.com.authapplication.*

class DialogRegister: DialogFragment() {
    private lateinit var editEmail          : EditText
    private lateinit var editPassword       : EditText
    private lateinit var editConfirmPassword  : EditText
    private lateinit var dialog             : AlertDialog
    var onRegisterUser: ((email: String, password: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = activity?.layoutInflater!!.inflate(R.layout.dialog_register, null)
        editEmail           = root.findViewById(R.id.editTextEmail)
        editPassword        = root.findViewById(R.id.editTextPassword)
        editConfirmPassword = root.findViewById(R.id.editTextConfirmPassword)
        editEmail.setText(arguments?.getString("email"))
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.dlgreg_authentication_title)
            .setView(root)
            .setNegativeButton(R.string.button_cancel, null)
            .setPositiveButton(R.string.dlgreg_button, null)
        dialog = builder.create()
        setDialogStyle(dialog)
        return dialog
    }


    private fun performRegister(){
        fun validateEmail(): Boolean{
            val isCorrect = validateMail(editEmail.text.toString())
            if (!isCorrect)
                editEmail.error = getStringResource(R.string.incorrect_email)
            return isCorrect
        }
        fun validatePassword(): Boolean{
            val password = editPassword.text.toString()
            val isCorrect = !(password.isBlank() || password.length < 5)
            if (!isCorrect)
                editPassword.error = getStringResource(R.string.dlgreg_error_password)
            return isCorrect
        }
        fun validateConfirmPassword(): Boolean{
            val password = editPassword.text.toString()
            val passwordConfirm = editConfirmPassword.text.toString()
            val isCorrect = !(passwordConfirm.isBlank() || password != passwordConfirm)
            if (!isCorrect)
                editConfirmPassword.error = getStringResource(R.string.dlgreg_error_passwordcheck)
            return isCorrect
        }
        editEmail.error = null
        editPassword.error = null
        editConfirmPassword.error = null
        if (!validateEmail() || !validatePassword() || !validateConfirmPassword())
            return
        dismiss()
        onRegisterUser?.invoke(editEmail.text.toString(), editPassword.text.toString())
    }

    override fun onStart() {
        super.onStart()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            performRegister()
        }
    }

}