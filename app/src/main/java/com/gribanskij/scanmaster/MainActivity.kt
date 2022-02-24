package com.gribanskij.scanmaster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.gribanskij.scanmaster.cameraX.*
import com.gribanskij.scanmaster.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Utils.allPermissionsGranted(this)) {
            Utils.requestRuntimePermissions(this)
        }
    }
}
