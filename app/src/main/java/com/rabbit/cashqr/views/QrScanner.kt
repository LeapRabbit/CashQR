package com.rabbit.cashqr.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp

import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border

import androidx.lifecycle.LifecycleOwner
import com.rabbit.cashqr.viewmodels.QrScannerViewModel

@Composable
fun CameraPreview(
    isScanning: Boolean,
    lifecycleOwner: LifecycleOwner,
    viewModel: QrScannerViewModel
) {
    val context = LocalContext.current
    val DarkOrange = Color(0xFFE2493D)

    // The PreviewView to display the camera feed
    val previewView = remember { PreviewView(context) }

    // Use LaunchedEffect to start the scanner when the state or lifecycle changes
    LaunchedEffect(key1 = isScanning, key2 = lifecycleOwner) {
        if (isScanning) {
            viewModel.startScan(lifecycleOwner, context, previewView)
        }
    }

    // AndroidView is used to embed the PreviewView (a traditional Android view) into the Compose hierarchy
    AndroidView(
        factory = { previewView },
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp)) // <-- Adds rounded corners
            .border(
                width = 2.dp,
                color = DarkOrange,
                shape = RoundedCornerShape(8.dp) // <-- Shapes the border
            )
    )
}