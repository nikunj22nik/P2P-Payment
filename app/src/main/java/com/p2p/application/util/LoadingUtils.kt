package com.p2p.application.util


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.p2p.application.R

class LoadingUtils {
    companion object {
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

        private const val LOADER_TAG = "APP_GLOBAL_LOADER"

        fun show(activity: Activity, transparent: Boolean = true) {
            val decorView = activity.window.decorView as ViewGroup
            val existing = decorView.findViewWithTag<View>(LOADER_TAG)
            if (existing != null) return
            val containerParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply { gravity = Gravity.CENTER }
            val container = FrameLayout(activity).apply {
                tag = LOADER_TAG
                setBackgroundColor(if (transparent) Color.TRANSPARENT else Color.parseColor("#66000000"))
                isClickable = true
                isFocusable = true
            }
            val sizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                60f,
                activity.resources.displayMetrics
            ).toInt()
            val progressParams = FrameLayout.LayoutParams(sizePx, sizePx).apply {
                gravity = Gravity.CENTER
            }
            val progressBar = ProgressBar(activity).apply {
                isIndeterminate = true
            }
            DrawableCompat.setTint(DrawableCompat.wrap(progressBar.indeterminateDrawable), Color.RED)
            container.addView(progressBar, progressParams)
            decorView.addView(container, containerParams)
        }

        fun hide(activity: Activity) {
            val decorView = activity.window.decorView as ViewGroup
            val loader = decorView.findViewWithTag<View>(LOADER_TAG)
            loader?.let { decorView.removeView(it) }
        }


        fun isOnline(context: Context?): Boolean {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true // Fast
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    when {
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                                networkCapabilities.linkDownstreamBandwidthKbps > 2000 -> true // Assume good speed
                        else -> false
                    }
                }
                else -> false
            }
        }

    fun ensurePeriod(input: String): String {
        return if (input.endsWith(".")) input else "$input."
    }

        }
    }

