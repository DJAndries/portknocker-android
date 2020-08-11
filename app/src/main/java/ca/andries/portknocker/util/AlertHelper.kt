package ca.andries.portknocker.util

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import ca.andries.portknocker.R

class AlertHelper {
    companion object {
        fun showConfirmDialog(context: Context, bodyRes: Int, confirmFunc: () -> Unit) {
            val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.confirm)
                .setMessage(bodyRes)
                .setPositiveButton(R.string.confirm) { _: DialogInterface, _: Int ->
                    confirmFunc()
                }
                .setNegativeButton(R.string.cancel, null)
                .create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            }

            dialog.show()
        }
    }
}