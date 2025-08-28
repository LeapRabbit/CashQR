package com.rabbit.cashqr.utils;

import com.rabbit.cashqr.data.model.UpiDetails
import java.net.URLDecoder


class UpiDetails(qrData: String) {
    private var queryData = ""
    private var queryMap = mutableMapOf<String, String>()

    init {
        val queryIndex = qrData.indexOf('?')
        if (queryIndex != -1 && queryIndex < qrData.length - 1) {
            queryData = qrData.substring(queryIndex + 1)
            val pairs = queryData.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (pair in pairs) {
                val equalsIndex = pair.indexOf('=')
                if (equalsIndex > 0) {
                    val key = pair.substring(0, equalsIndex)
                    val value = pair.substring(equalsIndex + 1)
                    queryMap.put(key, value)
                }
            }
        }
    }

    fun getQueryMap(): Map<String, String> {
        return queryMap
    }

    fun getQueryData(): String {
        return queryData
    }

    fun getUpiId(): String {
        return if(!queryMap["pa"].isNullOrEmpty())
            URLDecoder.decode(queryMap["pa"], "UTF-8")
        else
            ""
    }

    fun getUpiName(): String {
        return if(!queryMap["pn"].isNullOrEmpty())
            URLDecoder.decode(queryMap["pn"], "UTF-8")
        else
            ""
    }

    fun getUpiMcc(): String {
        return if(!queryMap["mc"].isNullOrEmpty())
            URLDecoder.decode(queryMap["mc"], "UTF-8")
        else
            ""
    }
}