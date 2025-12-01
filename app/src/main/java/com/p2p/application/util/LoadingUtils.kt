package com.p2p.application.util


import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.p2p.application.R

class LoadingUtils {
    companion object{
        fun showErrorDialog(context: Context?, text: String) {

            if (context == null) return

            // Inflate the custom layout
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.dialog_error, null)

            // Find views
            val errorMessage = dialogView.findViewById<TextView>(R.id.text)
            //  val errorIcon = dialogView.findViewById<ImageView>(R.id.errorIcon)
            val okButton = dialogView.findViewById<TextView>(R.id.textOkayButton)
            val cancelBtn = dialogView.findViewById<ImageView>(R.id.imageCross)

            // Set the error message
            errorMessage.text = ensurePeriod(text)

            // Create the dialog
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create()



            cancelBtn.setOnClickListener {
                dialog?.dismiss()
            }

            // Set button click listener
            okButton.setOnClickListener {
                dialog?.dismiss()
            }

            // Show the dialog
            dialog?.show()
        }



    fun ensurePeriod(input: String): String {
        return if (input.endsWith(".")) input else "$input."
    }
        }

}