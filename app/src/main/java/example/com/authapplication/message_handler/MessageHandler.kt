package example.com.authapplication.message_handler

import android.content.Context
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.widget.Toast
import example.com.authapplication.R
import example.com.authapplication.data.AuthValue
import example.com.authapplication.getStringResource

class MessageHandler {
    companion object {
        fun showToast(message: String, context: Context) {
            val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            val centeredText: Spannable = SpannableString(message)
            centeredText.setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0, message.length - 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            toast.show()
        }

        fun showError(error: AuthValue, context: Context) {
            val idErrorMessage =
                when (error) {
                    AuthValue.ERROR_CONNECTION -> {
                        R.string.error_connected_internet
                    }
                    AuthValue.ERROR_ALREADY_EMAIL -> {
                        R.string.error_already_email
                    }
                    AuthValue.ERROR_USER_DATA -> {
                        R.string.error_login_message
                    }
                    AuthValue.ERROR_RESTORE -> {
                        R.string.error_restore_password
                    }
                    else -> {
                        //AuthValue.ERROR_AUTH_SERVICE ->{
                        R.string.error_auth_service
                    }

                }
            showToast(getStringResource(idErrorMessage), context)
        }
    }
}