package com.rabbit.cashqr.viewmodels

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.rabbit.cashqr.data.model.UpiDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.util.concurrent.Executors


class QrScannerViewModel : ViewModel() {

    // StateFlow to hold the scanned QR code data, and a private mutable version
    private val _qrCodeData = MutableStateFlow("")
    val upiDetails: StateFlow<String> = _qrCodeData.asStateFlow()

    // StateFlow to control if scanning is active
    private val _isScanning = MutableStateFlow(true)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // A single thread executor for the image analysis to avoid blocking the main thread
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    // Use cases for the camera, will be bound and unbound by the ViewModel
    private var previewUseCase: Preview? = null
    private var imageAnalysisUseCase: ImageAnalysis? = null

    // Handles the lifecycle of the camera
    private var cameraProvider: ProcessCameraProvider? = null

    /**
     * This function initializes and starts the camera to scan for QR codes.
     * It binds the camera to the lifecycle of the composable.
     * @param lifecycleOwner The lifecycle owner for the camera.
     * @param context The application context.
     * @param previewView The PreviewView from the UI to display the camera feed.
     */
    fun startScan(
        lifecycleOwner: LifecycleOwner,
        context: Context,
        previewView: PreviewView
    ) {
        viewModelScope.launch {
            if (cameraProvider == null) {
                cameraProvider = ProcessCameraProvider.getInstance(context).get()
            }
            if (_isScanning.value) {
                bindCameraUseCases(lifecycleOwner, previewView)
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun bindCameraUseCases(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        // Set up the Preview use case
        previewUseCase = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        // Set up the ImageAnalysis use case with the BarcodeScanner
        imageAnalysisUseCase = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        val options = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
                        val scanner: BarcodeScanner = BarcodeScanning.getClient(options)

                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                if (barcodes.isNotEmpty()) {
                                    val firstBarcode = barcodes.first()
                                    firstBarcode.rawValue?.let { rawValue ->
                                        // Update state with the scanned value and stop scanning
                                        _qrCodeData.value = rawValue
                                        _isScanning.value = false
                                        // Unbind camera use cases
                                        cameraProvider?.unbindAll()
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("QrScannerViewModel", "Barcode processing failed: ${e.message}")
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }
            }

        // Select the back camera as the default
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Unbind all use cases before rebinding
            cameraProvider?.unbindAll()

            // Bind the camera and its use cases to the lifecycle
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                previewUseCase,
                imageAnalysisUseCase
            )
        } catch (exc: Exception) {
            Log.e("QrScannerViewModel", "Use case binding failed", exc)
        }
    }

    /**
     * Resets the scanner to allow a new QR code to be scanned.
     */
    fun resetScanner() {
        _qrCodeData.value = ""
        _isScanning.value = true
        // The startScan function will be called again by the Composable when the state changes
    }

    // Shut down the executor when the ViewModel is no longer used
    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }
}