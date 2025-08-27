package com.rabbit.cashqr.data.repository

import android.content.Context
import com.rabbit.cashqr.R
import com.rabbit.cashqr.data.model.CreditCard
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

class CcDetailsRepository (context: Context) {
    // Read the JSON file from res/raw
    private val inputStream = context.resources.openRawResource(R.raw.data)
    private val reader = BufferedReader(InputStreamReader(inputStream))
    private val jsonString = reader.use { it.readText() }
    private val data: List<CreditCard> = Json.decodeFromString<List<CreditCard>>(jsonString)

    // StateFlow to hold the list of CC, and a private mutable version
    private val ccList = data

    /**
     * Resets eligible CCs
     */
    fun getCcList(): List<CreditCard> {
        return ccList
    }
}