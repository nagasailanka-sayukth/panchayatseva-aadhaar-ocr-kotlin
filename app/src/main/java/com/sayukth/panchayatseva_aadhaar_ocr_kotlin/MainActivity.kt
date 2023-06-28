package com.sayukth.panchayatseva_aadhaar_ocr_kotlin

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sayukth.aadhaar_ocr_utils_kotlin.constants.Constants
import com.sayukth.aadhaar_ocr_utils_kotlin.functionality.DetectAadhaarActivity
import com.sayukth.aadhaar_ocr_utils_kotlin.functionality.DetectAadhaarContract
import com.sayukth.aadhaar_ocr_utils_kotlin.functionality.DetectAadhaarPresenter
import com.sayukth.aadhaar_ocr_utils_kotlin.utils.StringSplitUtils
import com.sayukth.panchayatseva_aadhaar_ocr_kotlin.databinding.ActivityMainBinding
import java.io.IOException
import java.text.StringCharacterIterator
import java.util.Arrays

class MainActivity : AppCompatActivity(), DetectAadhaarContract.View {

    private val AADHAAR_REQUEST_IMAGE = 100
    private var presenter: DetectAadhaarContract.Presenter? = null
    private val fatherOrSpouseName: StringCharacterIterator? = null


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            presenter = DetectAadhaarPresenter(this, this@MainActivity)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun scanAadhaar(view: View?) {
        showAadhaarDetectOptions()
    }

    override fun showAadhaarDetectOptions() {
        Dexter.withActivity(this@MainActivity)
            .withPermissions(Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showCameraOptions()
                    } else {
                        // TODO - handle permission denied case
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<com.karumi.dexter.listener.PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    override fun showCameraOptions() {
        DetectAadhaarActivity.showImagePickerOptions(this, object :
            DetectAadhaarActivity.PickerOptionListener {
            override fun onTakeCameraSelected() {
                launchCameraIntent()
            }
        })
    }

    override fun showAadhaarInfo(map: HashMap<String?, String?>?) {
        try {


            map?.forEach { (key, value) ->
                Log.i("ocr info : ", "Key = $key :  Value = $value")
            }



            val aadhaarData = StringBuilder("Aadhaar Data : \n")
            //            Log.i("MainActivity", map + " ");
            var aadharId = map!!["AADHAR"]
            if (aadharId != null) {
                aadharId = aadharId.replace("\\s".toRegex(), "")
                aadhaarData.append(" Aadhaar Id : $aadharId, \n")
            }
            val name = map!!["NAME"]
            if (name != null && name !== " ") {
                aadhaarData.append(
                    """
                ${" Sur Name : " + StringSplitUtils.getFirstPartOfStringBySplitString(name, " ")}, 
                
                """.trimIndent()
                )
                aadhaarData.append(
                    """
                ${"  Name : " + StringSplitUtils.getLastPartOfStringBySplitString(name, " ")}, 
                
                """.trimIndent()
                )
            }
            val fsname = map!!["FATHER"]
            if (fsname != null && fsname !== " ") {
                aadhaarData.append(" FatherNameorSpouse : $fsname, \n")
            }
            val dob = map!!["DATE_OF_YEAR"]
            if (dob != null) {
                aadhaarData.append(" Dob : $dob , \n")
            }
            val genderStr = map!!["GENDER"]
            if (genderStr !== "" && genderStr != null) {
                if (genderStr == "M" || genderStr.startsWith("M")) {
                    aadhaarData.append(
                        """ Gender : Male , 
"""
                    )
                } else if (genderStr == "F" || genderStr.startsWith("F")) {
                    aadhaarData.append(
                        """ Gender : Female , """
                    )
                } else {
                    aadhaarData.append(
                        """ Gender : Other , """
                    )
                }
            }

//            String Mobile = map.get("Mobile");
//            if (dob != null) {
//                aadhaarData.append(" Dob : " + dob + ", \n");
            binding.tvAadhaarData!!.text = aadhaarData
            val fsNameStr = map!!["FATHER"]
            //            PanchayatSevaUtilities.showToast(otherStr + " ");
            if (fsNameStr != null && fsNameStr != " ") {
                val nameRegex = "^[a-zA-Z\\s]*$"
                val fsNameStr =
                    Arrays.toString(fsNameStr.split(nameRegex.toRegex(), limit = 1).toTypedArray())
                fatherOrSpouseName?.setText(fsNameStr)
                aadhaarData.append("FATHER NAME:").append(fatherOrSpouseName).append(" ,\n")
                //                   String fsNameStr = PanchayatSevaUtilities.splitString(otherStr);
//                    fatherOrSpouseName.setText(PanchayatSevaUtilities.stringToTitleCaseString(fsNameStr));
            }




            val otherStr = map!!["OTHER"]

            if (otherStr != null && otherStr != " ") {

            }










        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showImageText(imageText: String?) {
        if (!imageText.isNullOrEmpty()) {
            binding.tvOcrImageData!!.text = imageText
        }
    }

    private fun launchCameraIntent() {
        val intent = Intent(this@MainActivity, DetectAadhaarActivity::class.java)
        intent.putExtra(
            DetectAadhaarActivity.INTENT_IMAGE_PICKER_OPTION,
            DetectAadhaarActivity.REQUEST_IMAGE_CAPTURE
        )

        // setting aspect ratio
        intent.putExtra(DetectAadhaarActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(DetectAadhaarActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(DetectAadhaarActivity.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(DetectAadhaarActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(DetectAadhaarActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(DetectAadhaarActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)
        startActivityForResult(intent, Constants.AADHAAR_REQUEST_IMAGE)
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        try {
            if (requestCode == Constants.AADHAAR_REQUEST_IMAGE) {
                if (resultCode == RESULT_OK) {
                    val uri = intent?.getParcelableExtra<Uri>("path")
                    try {
                        // You can update this bitmap to your server
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                        //                        extractText(bitmap);
                        binding.ivOcr.setImageBitmap(bitmap)
                        presenter!!.getImageDataAsText(bitmap)
                        // loading profile image from local cache
//                        loadProfile(uri.toString());
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}