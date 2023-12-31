package com.sayukth.aadhaar_ocr_utils_kotlin.functionality

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.sayukth.aadhaar_ocr_utils_kotlin.constants.Constants
import com.sayukth.aadhaar_ocr_utils_kotlin.error.ActivityException
import com.sayukth.aadhaar_ocr_utils_kotlin.shared_preferences.AadhaarOcrPreferences
import com.sayukth.aadhaar_ocr_utils_kotlin.utils.DateUtils
import com.sayukth.aadhaar_ocr_utils_kotlin.utils.StringSplitUtils
import com.sayukth.aadhaar_ocr_utils_kotlin.utils.ToastUtils
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class DetectAadhaarPresenter(
    private val detectAadhaarView: DetectAadhaarContract.View,
    private val activity: Activity
) : DetectAadhaarContract.Presenter {

    companion object {
        private const val TAG = "DetectAadhaarPresent"
    }

    var metadataMap = HashMap<String?, String?>()

    override fun getImageDataAsText(photo: Bitmap?) {

//        val aadhaarOcrScanSide: String = AadhaarOcrPreferences.getInstance()
//            ?.getString(AadhaarOcrPreferences.Key.AADHAAR_OCR_SCAN_SIDE, "") ?: ""

        val textRecognizer = TextRecognizer.Builder(activity).build()
        val imageFrame = Frame.Builder().setBitmap(photo).build()

        var imageText = ""
        val stringBuilder = StringBuilder()
        val textBlockSparseArray: SparseArray<TextBlock> = textRecognizer.detect(imageFrame)
        for (i in 0 until textBlockSparseArray.size()) {
            val textBlock: TextBlock = textBlockSparseArray[textBlockSparseArray.keyAt(i)]
            imageText = textBlock.getValue()
            Log.d("Language : ", imageText + " : " + textBlock.getLanguage())
            stringBuilder.append("#$imageText#")
            stringBuilder.append("\n")



//            if (aadhaarOcrScanSide == Constants.AADHAAR_OCR_BACK_SIDE) {

//                getFSTextType(imageText)
//                setFatherOrSpouseMetaData(imageText)

//            } else if (aadhaarOcrScanSide == Constants.AADHAAR_OCR_FRONT_SIDE) {
                getTextType(imageText)
//            }

        }


        detectAadhaarView.showAadhaarInfo(metadataMap)
    }

    fun getFSTextType(`val`: String) {
        try {
            val type = ""
            if (`val`.contains("\n")) {
                val valArr = `val`.split("\n").toTypedArray()
                if (valArr.size > 0) {
                    for (newlineIdx in valArr.indices) {
                        Log.i("OCR String Builder $newlineIdx : ", valArr[newlineIdx].toString())
                        setFatherOrSpouseMetaData(valArr[newlineIdx])
                    }
                }
            } else {
                Log.i("OCR String Builder $ : ", `val`)
                setFatherOrSpouseMetaData(`val`)
            }
        } catch (e: ActivityException) {
        }
    }

    fun getTextType(`val`: String) {
        try {
            val type = ""
            if (`val`.contains("\n")) {
                val valArr = `val`.split("\n").toTypedArray()
                if (valArr.size > 0) {
                    for (newlineIdx in valArr.indices) {
                        Log.i("OCR String Builder $newlineIdx : ", valArr[newlineIdx].toString())
                        setMetaData(valArr[newlineIdx])
                    }
                }
            } else {
                Log.i("OCR String Builder $ : ", `val`)
                setMetaData(`val`)
            }
        } catch (e: ActivityException) {
        }
    }


    @Throws(ActivityException::class)
    fun setFatherOrSpouseMetaData(`val`: String) {
        val srcVal = `val`.uppercase(Locale.getDefault())
        if (srcVal.contains("ADDRESS")) {
            val metaData = "FATHER"
            val text: String = StringSplitUtils.getLastPartOfStringBySplitString(`val`, ":")
            val fsNameWithCareOf: String = StringSplitUtils.getFirstPartOfStringBySplitString(text, ",")
            val fsName: String = StringSplitUtils.getLastPartOfStringBySplitString(fsNameWithCareOf.trim { it <= ' ' }, " ")
            metadataMap[metaData] = fsName.trim { it <= ' ' }
        } else {
            ToastUtils.showLongToast("Please Scan Aadhaar Back Side Accurately", activity)
        }
    }

    @Throws(ActivityException::class)
    fun setMetaData(metaDataVal: String) {
        try {
            val aadharRegex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$"
            val nameRegex = "^[a-zA-Z\\s]*$"
            val aadharMatcher = getPatternMatcher(aadharRegex, metaDataVal)
            val nameMatcher = getPatternMatcher(nameRegex, metaDataVal)
            var metaData = "OTHER"
            val srcVal = metaDataVal.toUpperCase(Locale.ROOT)
            var tgtVal = metaDataVal
            if (srcVal.contains("MALE") || srcVal.contains("FEMALE") || srcVal.contains("TRANS")) {
                metaData = "GENDER"
                tgtVal = if (metaDataVal.contains("/")) {
                    metaDataVal.split("/").toTypedArray()[1]
                } else {
                    metaDataVal.split(" ").toTypedArray()[1]
                }
            } else if (srcVal.contains("YEAR") || srcVal.contains("BIRTH") || srcVal.contains("DATE") || srcVal.contains(
                    "DOB"
                ) ||
                srcVal.contains("YEAR OF") || srcVal.contains("YOB")
            ) {
                metaData = "DATE_OF_YEAR"
                tgtVal = if (metaDataVal.contains(":")) {
                    metaDataVal.split(":").toTypedArray()[1]
                } else {
                    val dobValArr = metaDataVal.split(" ").toTypedArray()
                    val dobValLen = dobValArr.size
                    dobValArr[dobValLen - 1]
                }
                tgtVal = getFormatedDate(tgtVal)
            } else if (aadharMatcher.matches()) {
                metaData = "AADHAR"
            } else if (nameMatcher.matches() && !srcVal.contains("GOVERNMENT") && !srcVal.contains("INDIA") && !srcVal.contains(
                    "FATHER"
                )
            ) {
                metaData = "NAME"
            }
            metadataMap[metaData] = tgtVal.trim { it <= ' ' }
        } catch (e: ActivityException) {
            e.message?.let { Log.i(TAG, it) }
            throw ActivityException(e)
        }
    }

    @Throws(ActivityException::class)
    private fun getFormatedDate(datevalue: String): String {
        var datevalue: String? = datevalue
        return try {
            datevalue =
                if (datevalue != null && !datevalue.isEmpty()) datevalue.trim { it <= ' ' } else ""
            if (datevalue.matches(Regex("\\d{4}"))) {
                //This block will execute when we have only year in the aadhaar caed
                "01-01-$datevalue"
            } else {
                DateUtils.aAdhaarDateFormated(datevalue)!!
            }
        } catch (execption: ActivityException) {
            throw ActivityException(execption)
        }
    }

    private fun getPatternMatcher(regex: String, value: String): Matcher {
        val pattern = Pattern.compile(regex)
        return pattern.matcher(value)
    }


}


