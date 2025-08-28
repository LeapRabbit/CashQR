package com.rabbit.cashqr.views

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.rabbit.cashqr.R
import com.rabbit.cashqr.utils.UpiDetails
import com.rabbit.cashqr.viewmodels.QrScannerViewModel

// Define some custom colors for the UI
val DarkBlue = Color(0xFF1A1D2B)
val LightOrange = Color(0xFFF9665B)
val DarkOrange = Color(0xFFE2493D)
val OrangeGradient = Brush.verticalGradient(
    colors = listOf(LightOrange, DarkOrange)
)
val CardColor = Color(0xFF2E3141)
val PlaceholderImageColor = Color(0xFFD3D3D3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardBasedScreen(viewModel: QrScannerViewModel) {
    val qrData by viewModel.upiDetails.collectAsState()
    val upiDetails = UpiDetails(qrData)
    val context = LocalContext.current
    val activity = (context as? Activity)
    SetStatusBarColorNative(DarkBlue)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBlue,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.resetScanner() },
                containerColor = LightOrange,
                shape = CircleShape,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.qr),
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier
                        .size(60.dp)
                )
            }
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top App Bar
            TopAppBar(
                title = "CashQR",
                onBackClick = { activity?.finishAffinity() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main QR Code Card
            MainQrCodeCard(viewModel)

            Spacer(modifier = Modifier.height(24.dp))

            if (qrData.isNotEmpty()) {
                if (upiDetails.getUpiMcc().isEmpty()) {
                    UpiCard(context, qrData)
                } else {
                    // Section Headers
                    HeaderSection(title = "Suggested Payment Methods")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reward List Items
                    CcList(context, qrData)
                }

            }
        }
    }
}

@Composable
fun SetStatusBarColorNative(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = color.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
}

@Composable
fun TopAppBar(title: String, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

    }
}

@Composable
fun MainQrCodeCard(viewModel: QrScannerViewModel) {
    val qrData by viewModel.upiDetails.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val upiDetails = UpiDetails(qrData)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(OrangeGradient)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                // QR Code and Checkmark
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(8.dp)
                        .width(100.dp)
                ) {
                    // Placeholder for a QR Code image
                    CameraPreview(
                        isScanning = isScanning,
                        lifecycleOwner = lifecycleOwner,
                        viewModel = viewModel
                    )
                }

                // Credit Card Text
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(top = 8.dp, end = 8.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (upiDetails.getUpiId().isEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .width(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Scan QR",
                                color = Color.White,
                                fontSize = 16.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else if (upiDetails.getUpiMcc().isEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .width(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Unable to get MCC code",
                                color = Color.White,
                                fontSize = 16.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Text(
                            text = upiDetails.getUpiId(),
                            color = Color.White,
                            fontSize = 18.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = upiDetails.getUpiName(),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = upiDetails.getUpiMcc(),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

//class QrScannerViewModelProvider : PreviewParameterProvider<QrScannerViewModel> {
//    override val values = sequenceOf(QrScannerViewModel())
//    override val count: Int = 1
//}
//
//// Preview the full screen to see the result
//@Preview(showBackground = true)
//@Composable
//fun PreviewCardBasedScreen(@PreviewParameter(provider = QrScannerViewModelProvider::class) viewModel: QrScannerViewModel) {
//    CardBasedScreen(viewModel)
//}
