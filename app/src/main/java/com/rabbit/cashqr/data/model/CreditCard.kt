package com.rabbit.cashqr.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreditCard(
    var appName: String = "",
    var ccName: String = "",
    var mccExcluded: List<String> = listOf(),
    var eligible: Boolean = false,
    var reward: String = "0%",
    var intentLink: String = "",
)
