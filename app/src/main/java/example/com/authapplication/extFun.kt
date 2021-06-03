package example.com.authapplication

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.Patterns
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

const val defaultStringResource = ""
const val defaultColorResource  = Color.TRANSPARENT

fun validateMail(email: String): Boolean {
    return !(email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
}

fun getStringResource(id: Int): String =
        try {
            AuthApplication.applicationContext().getString(id)
        }
        catch (e: Exception){
            defaultStringResource
        }


fun getColorResource(id: Int): Int =
        try {
            ContextCompat.getColor(
                    AuthApplication.applicationContext(),
                    id
            )
        }
        catch (e: Exception){
            defaultColorResource
        }


fun setDialogStyle(dialog: AlertDialog, noTitle: Boolean = false) {
    with(dialog) {
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setBackgroundDrawableResource(R.drawable.background_dialog)
        if (noTitle)
            window?.requestFeature(Window.FEATURE_NO_TITLE)
    }
}


/*
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun setWidthDialog(dialog: AlertDialog, widthDP: Int){
    val width: Int = widthDP.px
    val height = ViewGroup.LayoutParams.WRAP_CONTENT
    dialog.window!!.setLayout(width, height)
}

fun log(value: String){
    Log.v("authapp", value.uppercase())
}*/
