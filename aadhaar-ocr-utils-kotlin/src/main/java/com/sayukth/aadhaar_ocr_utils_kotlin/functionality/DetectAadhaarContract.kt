package com.sayukth.aadhaar_ocr_utils_kotlin.functionality

import android.graphics.Bitmap

interface DetectAadhaarContract {

    interface View {

        fun showAadhaarDetectOptions()

        fun showCameraOptions()

        fun showAadhaarInfo(map: HashMap<String?, String?>?)

        fun showImageText(imageText: String?)


    }

    interface Presenter {

        fun getImageDataAsText(bitmap: Bitmap?)

    }

}