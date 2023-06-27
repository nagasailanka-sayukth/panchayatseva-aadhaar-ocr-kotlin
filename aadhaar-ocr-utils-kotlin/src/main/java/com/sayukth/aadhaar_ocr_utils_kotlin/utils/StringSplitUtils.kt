package com.sayukth.aadhaar_ocr_utils_kotlin.utils

import com.sayukth.aadhaar_ocr_utils_kotlin.error.ActivityException


object StringSplitUtils {
    @Throws(ActivityException::class)
    fun getFirstPartOfStringBySplitString(string: String, delimiter: String): String {
        return try {
            var firstWord = " "
            if (string.contains(delimiter)) {
                val arr = string.split(delimiter.toRegex(), limit = 2).toTypedArray()
                firstWord = arr[0]
            }
            firstWord
        } catch (e: Exception) {
            throw ActivityException(e)
        }
    }

    @Throws(ActivityException::class)
    fun getLastPartOfStringBySplitString(string: String, delimiter: String): String {
        return try {
            var theRest = ""
            if (string.contains(delimiter)) {
                val arr = string.split(delimiter.toRegex(), limit = 2).toTypedArray()
                theRest = arr[1]
            }
            theRest
        } catch (e: Exception) {
            throw ActivityException(e)
        }
    }
}