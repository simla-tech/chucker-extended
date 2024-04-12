package com.chuckerteam.chucker.internal.support

import android.content.Context
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import okio.Buffer
import okio.Source

internal class WsTransactionDetailsSharable(
    private val transaction: WsTransaction
) : Sharable {
    override fun toSharableContent(context: Context): Source = Buffer().apply {
        writeUtf8("${context.getString(R.string.chucker_extended_type)}: ${transaction.type?.name}\n")
        writeUtf8("${context.getString(R.string.chucker_url)}: ${transaction.host}\n")
        val isSsl =
            if (transaction.sslEnabled == true) R.string.chucker_yes else R.string.chucker_no
        writeUtf8("${context.getString(R.string.chucker_ssl)}: ${context.getString(isSsl)}\n")
        writeUtf8("${context.getString(R.string.chucker_extended_time)}: ${transaction.timestampString}\n")
        if (transaction.code != null || transaction.reason != null) {
            if (transaction.code != null) {
                writeUtf8("${context.getString(R.string.chucker_extended_code)}: ${transaction.code}\n")
            }
            if (transaction.reason != null) {
                writeUtf8("${context.getString(R.string.chucker_extended_reason)}: ${transaction.reason}\n")
            }
        }
        if (transaction.sizeMessage != null) {
            writeUtf8("${context.getString(R.string.chucker_extended_size)}: ${transaction.sizeMessageString}\n\n")
            writeUtf8("---------- ${context.getString(R.string.chucker_extended_message)} ----------\n")
            if (transaction.textMessage != null) {
                writeUtf8("${FormatUtils.formatJsonWithGqlQuery(transaction.textMessage!!)}\n")
            } else {
                writeUtf8("${transaction.byteMessage}\n")
            }
        }
    }
}
