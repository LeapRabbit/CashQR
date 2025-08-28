package com.rabbit.cashqr.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.cashqr.R
import com.rabbit.cashqr.data.model.CreditCard
import com.rabbit.cashqr.data.repository.CcDetailsRepository
import com.rabbit.cashqr.utils.UpiDetails


@Composable
fun CcList(context: Context, qrData: String) {
    val ccList: List<CreditCard> = CcDetailsRepository(context).getCcList()
    val upiDetails = UpiDetails(qrData)
    val mcc = upiDetails.getUpiMcc()
    val upiLink = "upi://pay/"

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between items
    ) {
        items(ccList.size) { index ->
            if (mcc.isNotEmpty()) {
                val cc = ccList[index]
                val rID = getResourceId(cc.ccName)
                RewardCard(
                    icon = {
                        Image(
                            painter = painterResource(id = rID),
                            contentDescription = cc.ccName,
                            modifier = Modifier
                                .size(80.dp)
                        )
                    },
                    title = cc.appName,
                    subtitle = cc.ccName,
                    reward = if (cc.mccExcluded.contains(mcc)) cc.reward else "0 %",
                    {
                        var intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(cc.intentLink.plus("?" + upiDetails.getQueryData()))
                        );
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(intent);
                        } else {
                            intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiLink));
                            context.startActivity(intent);
                        }
                    }

                )
            }
        }
    }
}

@Composable
fun RewardCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    reward: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                icon()

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
            SubmitButton(onClick, reward)
        }
    }
}

@Composable
fun SubmitButton(onClick: () -> Unit, reward: String) {
    Button(
        onClick,
        modifier = Modifier
            .width(150.dp) // Makes the button take the full width
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (reward == "0 %") Color(0xFFF44336) else Color(0xFF4CAF50), // A custom green color
            contentColor = Color.White // Text color
        )
    ) {
        Text(reward)
    }
}

private fun getResourceId(res: String): Int {
    when (res) {
        "AU Kosmos" -> return R.drawable.au_kosmos
        "SBI Select Black" -> return R.drawable.sbi_select_black
        else -> return R.drawable.cc
    }
}