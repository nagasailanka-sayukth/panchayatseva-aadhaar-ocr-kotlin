package com.sayukth.aadhaar_ocr_utils_kotlin

import android.app.Application
import android.content.Context


class AadhaarOcrUtilsKotlinApplication : Application() {

    companion object {
        lateinit var INSTANCE: AadhaarOcrUtilsKotlinApplication

        /**
         * @return The singleton instance
         */
        fun getApp(): AadhaarOcrUtilsKotlinApplication {
            return INSTANCE
        }

        fun getAppContext(): Context {
            return INSTANCE
        }
    }

    init {
        INSTANCE = this
    }



    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }





}