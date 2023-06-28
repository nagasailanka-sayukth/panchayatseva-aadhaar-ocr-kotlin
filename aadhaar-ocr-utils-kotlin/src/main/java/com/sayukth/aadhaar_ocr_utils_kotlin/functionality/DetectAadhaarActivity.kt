package com.sayukth.aadhaar_ocr_utils_kotlin.functionality

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.sayukth.aadhaar_ocr_utils_kotlin.R
import com.sayukth.aadhaar_ocr_utils_kotlin.constants.Constants
import com.sayukth.aadhaar_ocr_utils_kotlin.databinding.ActivityDetectAadhaarBinding
import com.sayukth.aadhaar_ocr_utils_kotlin.shared_preferences.AadhaarOcrPreferences
import com.yalantis.ucrop.UCrop
import java.io.File

class DetectAadhaarActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetectAadhaarBinding


    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    interface PickerOptionListener {
        fun onTakeCameraSelected()
//        fun onChooseAadharScanner()
    }

    companion object {
        val INTENT_IMAGE_PICKER_OPTION = "image_picker_option"
        val INTENT_ASPECT_RATIO_X = "aspect_ratio_x"
        val INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y"
        val INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio"
        val INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality"
        val INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height"
        val INTENT_BITMAP_MAX_WIDTH = "max_width"
        val INTENT_BITMAP_MAX_HEIGHT = "max_height"
        val REQUEST_IMAGE_CAPTURE = 0
        val REQUEST_GALLERY_IMAGE = 1

        var fileName: String? = null
        private var lockAspectRatio = false
        private var setBitmapMaxWidthHeight: Boolean = false
        private var ASPECT_RATIO_X =
            16
        private var ASPECT_RATIO_Y: Int = 9
        private var bitmapMaxWidth: Int = 1000
        private var bitmapMaxHeight: Int = 1000
        private var IMAGE_COMPRESSION = 80

        private val TAG: String = DetectAadhaarActivity::class.java.getSimpleName()

//    var aadharInputTypeFlag = false


        private var aadharInputTypeFlag: Boolean = false

        fun showImagePickerOptions(
            context: Context,
            listener: PickerOptionListener
        ) {
            val layoutInflater = LayoutInflater.from(context)
            val promptView: View =
                layoutInflater.inflate(R.layout.detect_aadhaar_options_dialog, null)
            val alertD = AlertDialog.Builder(context).create()
            alertD.setTitle(context.getString(R.string.choose_aadhaar_options_title))
            val btnFrontAadhaarCapture = promptView.findViewById<View>(R.id.btn_front_aadhaar_capture) as Button
            val btnBackAadhaarCapture = promptView.findViewById<View>(R.id.btn_back_aadhaar_capture) as Button


//            val scanQr = promptView.findViewById<View>(R.id.small_qr_scan_btn) as Button
//            frontAadhaarCard.setOnClickListener {
//                //                PreferenceHelper.getInstance().put(AADHAAR_INPUT_TYPE, Constants.OCR);
//                listener.onTakeCameraSelected()
//                alertD.dismiss()
//                aadharInputTypeFlag = true
//            }
//            backAadhaarCard.setOnClickListener { //PreferenceHelper.getInstance().put(AADHAAR_INPUT_TYPE, Constants.OCR);
//                aadharInputTypeFlag = true
//                listener.onTakeCameraSelected()
//                alertD.dismiss()
//            }


            btnFrontAadhaarCapture.setOnClickListener {
                aadharInputTypeFlag = false
                AadhaarOcrPreferences.getInstance()?.put(AadhaarOcrPreferences.Key.AADHAAR_OCR_SCAN_SIDE, Constants.AADHAAR_OCR_FRONT_SIDE)
                listener.onTakeCameraSelected()
                alertD.dismiss()
            }


            btnBackAadhaarCapture.setOnClickListener {
                aadharInputTypeFlag = false
                AadhaarOcrPreferences.getInstance()?.put(AadhaarOcrPreferences.Key.AADHAAR_OCR_SCAN_SIDE, Constants.AADHAAR_OCR_BACK_SIDE)
                listener.onTakeCameraSelected()
                alertD.dismiss()
            }

            alertD.setView(promptView)
            alertD.show()

        }


    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String? {
        val returnCursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    fun clearCache(context: Context) {
        val path = File(context.externalCacheDir, "camera")
        if (path.exists() && path.isDirectory) {
            for (child in path.listFiles()) {
                child.delete()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectAadhaarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        if (intent == null) {
            Toast.makeText(
                applicationContext,
                getString(R.string.toast_image_intent_null),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        ASPECT_RATIO_X = intent.getIntExtra(
            INTENT_ASPECT_RATIO_X,
            ASPECT_RATIO_X
        )
        ASPECT_RATIO_Y = intent.getIntExtra(
            INTENT_ASPECT_RATIO_Y,
            ASPECT_RATIO_Y
        )
        IMAGE_COMPRESSION = intent.getIntExtra(
            INTENT_IMAGE_COMPRESSION_QUALITY,
            IMAGE_COMPRESSION
        )
        lockAspectRatio = intent.getBooleanExtra(
            INTENT_LOCK_ASPECT_RATIO,
            false
        )
        setBitmapMaxWidthHeight = intent.getBooleanExtra(
            INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT,
            false
        )
        bitmapMaxWidth = intent.getIntExtra(
            INTENT_BITMAP_MAX_WIDTH,
            bitmapMaxWidth
        )
        bitmapMaxHeight = intent.getIntExtra(
            INTENT_BITMAP_MAX_HEIGHT,
            bitmapMaxHeight
        )

        val requestCode = intent.getIntExtra(
            INTENT_IMAGE_PICKER_OPTION,
            -1
        )
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage()
        } else {
            chooseImageFromGallery()
        }



        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    doSomeOperations(data)
                } else {
                    setResultCancelled()
                }
            }
    }

    private fun doSomeOperations(data: Intent?) {
        print("Detaect Aadhaar" + data)
    }

    private fun takeCameraImage() {
        fileName = System.currentTimeMillis().toString() + ".jpg"
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher?.launch(takePictureIntent)


//        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        fileName =
                            System.currentTimeMillis().toString() + ".jpg"
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            getCacheImagePath(fileName!!)
                        )
                        if (takePictureIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(
                                takePictureIntent,
                                REQUEST_IMAGE_CAPTURE
                            )
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }



    private fun chooseImageFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        val pickPhoto = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(
                            pickPhoto,
                            REQUEST_GALLERY_IMAGE
                        )
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) {
                fileName?.let { getCacheImagePath(it)?.let { cropImage(it) } }
            } else {
                setResultCancelled()
            }

            REQUEST_GALLERY_IMAGE -> if (resultCode == RESULT_OK) {
                val imageUri = data?.data
                imageUri?.let { cropImage(it) }
            } else {
                setResultCancelled()
            }

            UCrop.REQUEST_CROP -> if (resultCode == RESULT_OK) {
                handleUCropResult(data)
            } else {
                setResultCancelled()
            }

            UCrop.RESULT_ERROR -> {
                val cropError: Throwable? = data?.let { UCrop.getError(it) }
                Log.e(
                    TAG,
                    "Crop error: $cropError"
                )
                setResultCancelled()
            }

            else -> setResultCancelled()
        }
    }

    private fun cropImage(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(
            File(
                cacheDir, queryName(
                    contentResolver, sourceUri
                )
            )
        )
        val options: UCrop.Options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary))
        if (lockAspectRatio) options.withAspectRatio(ASPECT_RATIO_X.toFloat(),
            ASPECT_RATIO_Y.toFloat()
        )
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)
        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .start(this)
    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) {
            setResultCancelled()
            return
        }
        val resultUri = UCrop.getOutput(data)
        resultUri?.let { setResultOk(it) }
    }

    private fun setResultOk(imagePath: Uri) {
        val intent = Intent()
        intent.putExtra("path", imagePath)
        setResult(RESULT_OK, intent)
        //        if(aadharInputTypeFlag==true) {
//            PreferenceHelper.getInstance().put(AADHAAR_INPUT_TYPE, AadhaarInputType.OCR.name());
//
//        } else {
//            PreferenceHelper.getInstance().put(AADHAAR_INPUT_TYPE, AadhaarInputType.QRCODE.name());
//        }
        finish()
    }

    private fun setResultCancelled() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    private fun getCacheImagePath(fileName: String): Uri? {
        val path = File(externalCacheDir, "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return FileProvider.getUriForFile(
            this@DetectAadhaarActivity,
            "$packageName.provider", image
        )
    }




}