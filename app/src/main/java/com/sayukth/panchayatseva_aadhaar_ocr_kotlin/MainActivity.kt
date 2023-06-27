package com.sayukth.panchayatseva_aadhaar_ocr_kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sayukth.panchayatseva_aadhaar_ocr_kotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}