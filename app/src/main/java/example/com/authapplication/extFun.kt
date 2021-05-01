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
import java.util.*

fun validateMail(email: String): Boolean {
    return !(email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
}

fun getStringResource(id: Int): String =
        try {
            AuthApplication.applicationContext().getString(id)
        }
        catch (e: Exception){
            ""
        }

fun getColorResource(id: Int): Int =
        try {
            ContextCompat.getColor(
                    AuthApplication.applicationContext(),
                    id
            )
        }
        catch (e: Exception){
            Color.TRANSPARENT
        }

fun log(value: String){
    Log.v("authapp", value.toUpperCase(Locale.ROOT))
}

fun setDialogStyle(dialog: AlertDialog, noTitle: Boolean = false) {
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
    if (noTitle)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE);
}
/*val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()*/
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
fun setWidthDialog(dialog: AlertDialog, widthDP: Int){
    val width: Int = widthDP.px
    val height = ViewGroup.LayoutParams.WRAP_CONTENT
    dialog.window!!.setLayout(width, height)
}