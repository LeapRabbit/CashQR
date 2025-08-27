package com.rabbit.cashqr.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rabbit.cashqr.data.model.CreditCard
import com.rabbit.cashqr.data.repository.CcDetailsRepository


@Composable
fun CcList(context: Context, mcc: String) {
    val ccList: List<CreditCard> = CcDetailsRepository(context).getCcList()
    val upiLink = "upi://"
    val kiwiLink = "kiwi://upi"
    val phonepeLink = "phonepe://upi"

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp) // Add spacing between items
    ) {
        items(ccList.size) { index ->
            if (mcc.isNotEmpty()) {
                ScannedItemCard(
                    if (ccList[index].mccExcluded.contains(mcc)) "${ccList[index].name} : No Rewards"
                    else "${ccList[index].name} : Rewards",
                    if (ccList[index].mccExcluded.contains(mcc)) Color.Red else Color.Green,
                    { text ->
                        run {
                            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiLink));
                            if (text.contains("Kiwi")) {
                                intent = Intent(Intent.ACTION_VIEW, Uri.parse(kiwiLink))
                            } else if (text.contains("Phonepe")) {
                                intent = Intent(Intent.ACTION_VIEW, Uri.parse(phonepeLink))
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent);
                            } else {
                                intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiLink));
                                context.startActivity(intent);
                            }
                        }
                    })
            }
        }
    }
}

@Composable
fun ScannedItemCard(scannedText: String, color: Color, onCopyClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color // Change this color
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = scannedText,
                fontSize = 20.sp,
                modifier = Modifier.width(200.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onCopyClick(scannedText) }) {
                Text("Open")
            }
        }
    }
}