package com.sayukth.aadhaar_ocr_utils_kotlin.error


class ActivityException : Exception {
    constructor(e: Exception?) : super(e) {}
    constructor(message: String?) : super(message) {}
}