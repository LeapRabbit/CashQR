package com.rabbit.cashqr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.rabbit.cashqr.viewmodels.QrScannerViewModel
import com.rabbit.cashqr.views.QrScannerScreen

class MainActivity : ComponentActivity() {

    private val viewModel: QrScannerViewModel by viewModels()

    // Activity Result Launcher for requesting camera permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request camera permission on startup if not granted
        if (!isCameraPermissionGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            QrScannerScreen(viewModel = viewModel)
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}