package com.chuckerteam.chucker.internal.support

import android.content.Context
import com.chuckerteam.chucker.R.string
import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import okio.Buffer
import okio.Source

internal class WsTransactionListDetailsSharable(
    transactions: List<WsTransaction>,
) : Sharable {
    private val transactions = transactions.map { WsTransactionDetailsSharable(it) }

    override fun toSharableContent(context: Context): Source = Buffer().apply {
        writeUtf8(
            transactions.joinToString(
                separator = "\n${context.getString(string.chucker_export_separator)}\n",
                prefix = "${context.getString(string.chucker_export_prefix)}\n",
                postfix = "\n${context.getString(string.chucker_export_postfix)}\n"
            ) { it.toSharableUtf8Content(context) }
        )
    }
}
