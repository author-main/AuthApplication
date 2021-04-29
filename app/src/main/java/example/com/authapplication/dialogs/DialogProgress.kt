package example.com.authapplication.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import example.com.authapplication.R

class DialogProgress(context: Context): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress)
        setCancelable(false)
        window?.let {
            val layoutParams = WindowManager.LayoutParams()
            setContentView(R.layout.dialog_progress)
            layoutParams.copyFrom(it.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            it.attributes = layoutParams
            it.setBackgroundDrawableResource(android.R.color.transparent)
            it.setDimAmount(0F)
        }
    }
}