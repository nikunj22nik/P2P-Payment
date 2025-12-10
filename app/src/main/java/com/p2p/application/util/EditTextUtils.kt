package com.p2p.application.util

import android.text.InputType
import android.text.method.DigitsKeyListener
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText

object EditTextUtils {


    private class AsteriskPasswordTransformation : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: View?): CharSequence {
            return object : CharSequence {
                override val length: Int get() = source.length
                override fun get(index: Int): Char = '*'
                override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                    return "*".repeat(endIndex - startIndex)
                }
            }
        }
    }

    fun setNumericAsteriskPassword(editText: EditText) {
        // Set numeric keyboard
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.keyListener = DigitsKeyListener.getInstance("0123456789")

        // Set masking as *
        editText.transformationMethod = AsteriskPasswordTransformation()
    }
}