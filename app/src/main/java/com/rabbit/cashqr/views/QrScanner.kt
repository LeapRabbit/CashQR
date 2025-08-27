package com.rabbit.cashqr.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.LifecycleOwner
import com.rabbit.cashqr.viewmodels.QrScannerViewModel

@Composable
fun QrScannerScreen(viewModel: QrScannerViewModel) {
    val upiDetails by viewModel.upiDetails.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray)
            ) {
                CameraPreview(
                    isScanning = isScanning,
                    lifecycleOwner = lifecycleOwner,
                    viewModel = viewModel
                )
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ){
                    val annotatedString = buildAnnotatedString {
                        append("UPI : ")
                        withStyle(style = SpanStyle(color = Color.DarkGray)) {
                            append("${upiDetails.upiId} \n") // This word will be red
                        }
                        append("NAME : ")
                        withStyle(style = SpanStyle(color = Color.DarkGray)) {
                            append("${upiDetails.name} \n") // This word will be red
                        }
                        append("MCC : ")
                        withStyle(style = SpanStyle(color = Color.DarkGray)) {
                            append(upiDetails.mcc) // This word will be red
                        }
                    }

                    // Text view for displaying the scanned data
                    Text(
                        text = annotatedString,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Button to restart scanning
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    onClick = { viewModel.resetScanner() },
                    enabled = !isScanning // Only enabled when scanning is complete
                ) {
                    Text("Scan Again")
                }
            }
        }

        CcList(context, upiDetails.mcc)

    }
}

@Composable
fun CameraPreview(
    isScanning: Boolean,
    lifecycleOwner: LifecycleOwner,
    viewModel: QrScannerViewModel
) {
    val context = LocalContext.current

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
        modifier = Modifier.fillMaxSize()
    )
}