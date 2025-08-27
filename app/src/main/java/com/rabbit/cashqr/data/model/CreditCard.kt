package com.rabbit.cashqr.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreditCard(
    var name : String = "",
    var mccExcluded : List<String> = listOf(),
    var eligible : Boolean = false
)
