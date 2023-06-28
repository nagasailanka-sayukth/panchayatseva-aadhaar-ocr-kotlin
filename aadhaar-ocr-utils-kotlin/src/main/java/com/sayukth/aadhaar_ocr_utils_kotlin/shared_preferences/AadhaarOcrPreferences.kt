package com.sayukth.aadhaar_ocr_utils_kotlin.shared_preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.sayukth.aadhaar_ocr_utils_kotlin.AadhaarOcrUtilsKotlinApplication


class AadhaarOcrPreferences private constructor(context: Context) {
    private var aadhaarOcrPrefsBulkUpdate: Boolean = false

    lateinit var AADHAAR_OCR_IMAGE: ByteArray

    enum class Key {
        AADHAAR_OCR_SCAN_SIDE,
    }

    companion object {
        private const val SETTINGS_NAME = "aadhaar_ocr_kotlin_settings"

        private var aadhaarOcrPreferences: AadhaarOcrPreferences? = null
        private var sharedPrefs: SharedPreferences? = null
        private var sharedPrefsEditor: SharedPreferences.Editor? = null

        fun getInstance(context: Context): AadhaarOcrPreferences? {
            if (aadhaarOcrPreferences == null) {
                aadhaarOcrPreferences = AadhaarOcrPreferences(context.applicationContext)
            }

            return aadhaarOcrPreferences
        }

        fun getInstance(): AadhaarOcrPreferences? {
            if (aadhaarOcrPreferences != null) {
                return aadhaarOcrPreferences
            }

            return getInstance(AadhaarOcrUtilsKotlinApplication.getAppContext())
        }
    }

    init {
        sharedPrefs = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)
    }

    fun clear() {
        doEdit()
        sharedPrefsEditor?.clear()
        doCommit()
    }

    @SuppressLint("CommitPrefEdits")
    fun edit() {
        aadhaarOcrPrefsBulkUpdate = true
        sharedPrefsEditor = sharedPrefs?.edit()
    }

    fun commit() {
        aadhaarOcrPrefsBulkUpdate = false
        sharedPrefsEditor?.commit()
        sharedPrefsEditor = null
    }

    @SuppressLint("CommitPrefEdits")
    private fun doEdit() {
        if (!aadhaarOcrPrefsBulkUpdate && sharedPrefsEditor == null) {
            sharedPrefsEditor = sharedPrefs?.edit()
        }
    }

    private fun doCommit() {
        if (!aadhaarOcrPrefsBulkUpdate && sharedPrefsEditor != null) {
            sharedPrefsEditor?.commit()
            sharedPrefsEditor = null
        }
    }

    fun remove(vararg keys: Key) {
        doEdit()
        for (key in keys) {
            sharedPrefsEditor?.remove(key.name)
        }

        doCommit()
    }

    fun put(key: Key, value: String?) {
        doEdit()
        sharedPrefsEditor?.putString(key.name, value)
        doCommit()
    }

    fun put(key: Key, value: Int) {
        doEdit()
        sharedPrefsEditor?.putInt(key.name, value)
        doCommit()
    }

    fun put(key: Key, value: Boolean) {
        doEdit()
        sharedPrefsEditor?.putBoolean(key.name, value)
        doCommit()
    }

    fun put(key: Key, value: Float) {
        doEdit()
        sharedPrefsEditor?.putFloat(key.name, value)
        doCommit()
    }

    fun put(key: Key, value: Double) {
        doEdit()
        sharedPrefsEditor?.putString(key.name, value.toString())
        doCommit()
    }

    fun put(key: Key, value: Long) {
        doEdit()
        sharedPrefsEditor?.putLong(key.name, value)
        doCommit()
    }

    fun getString(key: Key, defaultValue: String?): String? {
        return sharedPrefs?.getString(key.name, defaultValue)
    }

    fun getString(key: Key): String? {
        return sharedPrefs?.getString(key.name, null)
    }

    fun getInt(key: Key): Int? {
        return sharedPrefs?.getInt(key.name, 0)
    }

    fun getInt(key: Key, defaultValue: Int): Int? {
        return sharedPrefs?.getInt(key.name, defaultValue)
    }

    fun getLong(key: Key): Long? {
        return sharedPrefs?.getLong(key.name, 0)
    }

    fun getLong(key: Key, defaultValue: Long): Long? {
        return sharedPrefs?.getLong(key.name, defaultValue)
    }

    fun getFloat(key: Key): Float? {
        return sharedPrefs?.getFloat(key.name, 0f)
    }

    fun getFloat(key: Key, defaultValue: Float): Float? {
        return sharedPrefs?.getFloat(key.name, defaultValue)
    }

    fun getDouble(key: Key): Double {
        return getDouble(key, 0.0)
    }

    fun getDouble(key: Key, defaultValue: Double): Double {
        return try {
            java.lang.Double.valueOf(
                sharedPrefs?.getString(
                    key.name,
                    defaultValue.toString()
                ) as String
            )
        } catch (nfe: NumberFormatException) {
            defaultValue
        }
    }

    fun getBoolean(key: Key, defaultValue: Boolean): Boolean? {
        return sharedPrefs?.getBoolean(key.name, defaultValue)
    }

    fun getBoolean(key: Key): Boolean? {
        return sharedPrefs?.getBoolean(key.name, false)
    }
}