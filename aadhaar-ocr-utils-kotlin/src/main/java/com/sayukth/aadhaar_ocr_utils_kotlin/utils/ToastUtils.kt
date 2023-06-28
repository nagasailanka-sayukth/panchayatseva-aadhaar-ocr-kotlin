package com.sayukth.aadhaar_ocr_utils_kotlin.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {

    fun showLongToast(msg: String, context: Context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun showShortToast(msg: String, context: Context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}