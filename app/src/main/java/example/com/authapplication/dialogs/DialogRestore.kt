package example.com.authapplication.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import example.com.authapplication.*

class DialogRestore: DialogFragment() {
    private lateinit var editEmail          : EditText
    private lateinit var dialog             : AlertDialog
    var onRestoreUser: ((email: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = activity?.layoutInflater!!.inflate(R.layout.dialog_restore, null)
        editEmail           = root.findViewById(R.id.editTextEmailRestore)
        editEmail.setText(arguments?.getString("email"))
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)
        builder.setTitle(R.string.dlgrest_title)
                .setView(root)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.dlgreg_button, null)
        dialog = builder.create()
        setDialogStyle(dialog)
        return dialog
    }


    private fun performRestore(){
        if (!validateMail(editEmail.text.toString())) {
            editEmail.error = getStringResource(R.string.incorrect_email)
            return
        }
        dismiss()
        onRestoreUser?.invoke(editEmail.text.toString())
    }

    override fun onStart() {
        super.onStart()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            performRestore()
        }
        setWidthDialog(dialog, 300)
    }

}