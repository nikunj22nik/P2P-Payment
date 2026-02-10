package com.p2p.application.util


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.Editable
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.NavController
import com.p2p.application.R
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong
import android.text.TextWatcher
import android.util.Log

import java.text.DecimalFormat
class LoadingUtils {


    companion object {

        private var dialogLoader: Dialog? = null

        fun showErrorDialog(context: Context?, text: String) {

            if (context == null) return

            val dialog= Dialog(context, R.style.BottomSheetDialog)

            dialog.setCancelable(false)

            dialog.setContentView(R.layout.error_alert)

            val layoutParams = WindowManager.LayoutParams()

            layoutParams.copyFrom(dialog.window!!.attributes)

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT

            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT

            dialog.window!!.attributes = layoutParams

            val btnOk: LinearLayout =dialog.findViewById(R.id.btnOk)

            val tvSubHeader: TextView =dialog.findViewById(R.id.tvSms)

            tvSubHeader.text=ensurePeriod(text)

            btnOk.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }

        fun showSessionDialog(context: Context?, text: String, navController: NavController) {

            if (context == null) return

            val dialog= Dialog(context, R.style.BottomSheetDialog)

            dialog.setCancelable(false)

            dialog.setContentView(R.layout.error_alert)

            val layoutParams = WindowManager.LayoutParams()

            layoutParams.copyFrom(dialog.window!!.attributes)

            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT

            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT

            dialog.window!!.attributes = layoutParams

            val btnOk: LinearLayout =dialog.findViewById(R.id.btnOk)

            val tvSubHeader: TextView =dialog.findViewById(R.id.tvSms)

            tvSubHeader.text=ensurePeriod(text)

            btnOk.setOnClickListener {
                dialog.dismiss()
                SessionManager(context).clearSession()
                navController.navigate(R.id.accountTypeFragment)
            }

            dialog.show()

        }
        private const val LOADER_TAG = "APP_GLOBAL_LOADER"

        fun show(activity: Activity, transparent: Boolean = true) {
            /*val decorView = activity.window.decorView as ViewGroup
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
            decorView.addView(container, containerParams)*/
            dialogLoader?.dismiss()
            dialogLoader = Dialog(activity)
            dialogLoader?.setContentView(R.layout.my_progess)
            dialogLoader?.setCancelable(false)
            dialogLoader?.window?.setDimAmount(0f)
            dialogLoader?.window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            dialogLoader?.show()
        }

        fun hide(activity: Activity) {
            if (dialogLoader != null) {
                dialogLoader?.dismiss()
            }
        }
        fun getPlainNumber(amount: String): String {

            return amount.replace(" ", "").replace(",", "")
        }

        fun EditText.addThousandSeparator() {
//            this.addTextChangedListener(object : TextWatcher {
//                private var current = ""
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//                override fun afterTextChanged(s: Editable?) {
//                    if (s.toString() != current) {
//                        this@addThousandSeparator.removeTextChangedListener(this)
//
//                        val cleanString = s.toString().replace(" ", "")
//
//                        if (cleanString.isNotEmpty()) {
//                            try {
//                                // Format using Indian numbering system
//                                val formatted = formatAmount(cleanString)
//
//                                current = formatted
//                                this@addThousandSeparator.setText(formatted)
//
//                                // Cursor at the end
//                                this@addThousandSeparator.setSelection(formatted.length)
//                            } catch (e: NumberFormatException) {
//                                // Fallback if number too big
//                                current = ""
//                                this@addThousandSeparator.setText("")
//                                this@addThousandSeparator.setSelection(0)
//                            }
//                        } else {
//                            current = ""
//                            this@addThousandSeparator.setText("")
//                            this@addThousandSeparator.setSelection(0)
//                        }
//
//                        this@addThousandSeparator.addTextChangedListener(this)
//                    }
//                }
//            })


            this.addTextChangedListener(object : TextWatcher {

                private var current = ""

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val newText = s?.toString() ?: return

                    if (newText == current) return

                    this@addThousandSeparator.removeTextChangedListener(this)

                    // 🔥 Step 1: clean input
                    val clean = newText
                        .replace(" ", "")
                        .replace("\u00A0", "")
                        .replace(",", "")

                    if (clean.isEmpty()) {
                        current = ""
                        this@addThousandSeparator.setText("")
                        this@addThousandSeparator.addTextChangedListener(this)
                        return
                    }

                    // 🔥 Step 2: handle decimal (200000.00)
                    val parts = clean.split(".")
                    val integerPart = parts[0]
                    val decimalPart = if (parts.size > 1) parts[1] else null

                    // 🔥 Step 3: manual 3-3 grouping
                    val formattedInt = integerPart
                        .reversed()
                        .chunked(3)
                        .joinToString(" ")
                        .reversed()

                    // 🔥 Step 4: attach decimal back
                    val formatted = if (decimalPart != null) {
                        "$formattedInt.$decimalPart"
                    } else {
                        formattedInt
                    }

                    current = formatted
                    this@addThousandSeparator.setText(formatted)

                    // cursor end par
                    this@addThousandSeparator.setSelection(formatted.length)

                    this@addThousandSeparator.addTextChangedListener(this)
                }
            })

        }

        fun formatAmount(amount: String): String {

//            val cleanAmount = amount.replace(" ", "").replace(",", "")
//            val number = cleanAmount.toDoubleOrNull()?.roundToLong() ?: return amount
//            val formatter = NumberFormat.getInstance(Locale("en", "IN"))
//            return formatter.format(number).replace(",", " ")
//
//            val cleanAmount = amount.replace(" ", "").replace(",", "")
//            val number = cleanAmount.toLongOrNull() ?: return amount
//
//            val formatter = NumberFormat.getInstance(Locale.FRANCE).apply {
//                isGroupingUsed = true
//                maximumFractionDigits = 0
//            }
//
//            return formatter.format(number)


//            val cleanAmount = amount.replace(" ", "").replace(",", "")
//            val number = cleanAmount.toLongOrNull() ?: return amount
//
//            val formatter = NumberFormat.getInstance(Locale.FRANCE).apply {
//                isGroupingUsed = true
//                maximumFractionDigits = 0
//            }
//
//            return formatter.format(number)

            if (amount.isEmpty()) return ""

            // 🔥 Step 1: remove separators
            val clean = amount
                .replace(" ", "")
                .replace("\u00A0", "")
                .replace(",", "")

            if (clean.isEmpty()) return ""

            // 🔥 Step 2: handle decimal input like 200000.00
            val parts = clean.split(".")
            val integerPart = parts[0]

            if (integerPart.isEmpty()) return amount

            // 🔥 Step 3: manual 3-3 grouping
            val formattedInt = integerPart
                .reversed()
                .chunked(3)
                .joinToString(" ")
                .reversed()

            // 🔥 Step 4: attach decimal back (if any)
            val output = if (parts.size > 1) {
                "$formattedInt.${parts[1]}"
            } else {
                formattedInt
            }

            Log.d("TESTING_NUMBERS", "Output is $output")

            return output
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

        fun toInitials(name: String): String {
            return name.trim()
                .split("\\s+".toRegex())
                .filter { it.isNotEmpty() }
                .joinToString("") { it[0].uppercase() }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun formatDateOnly(dateStr: String?): String {
            if (dateStr.isNullOrBlank()) return ""

            return try {
                val date = java.time.LocalDate.parse(
                    dateStr,
                    java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")
                )

                val today = java.time.LocalDate.now()
                val yesterday = today.minusDays(1)

                when (date) {
                    today -> "Today"
                    yesterday -> "Yesterday"
                    else -> date.format(
                        java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")
                    )
                }

            } catch (e: Exception) {
                ""
            }
        }


        fun getBitmapFromView(view: View): Bitmap {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }



        fun ensurePeriod(input: String): String {
        return if (input.endsWith(".")) input else "$input."
    }

        }
    }

