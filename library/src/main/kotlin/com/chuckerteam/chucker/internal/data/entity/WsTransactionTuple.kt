@file:Suppress("TooManyFunctions")

package com.chuckerteam.chucker.internal.data.entity

import androidx.room.ColumnInfo
import com.chuckerteam.chucker.internal.support.FormatUtils

/**
 * Represents WebSocket transaction. Instances of this classes
 * should be populated as soon as the library receives data from OkHttp.
 */
@Suppress("LongParameterList")
internal class WsTransactionTuple(
    @ColumnInfo(name = "id") var id: Long,
    @ColumnInfo(name = "host") var host: String?,
    @ColumnInfo(name = "sslEnabled") var sslEnabled: Boolean?,
    @ColumnInfo(name = "type") var type: WsTransaction.TransactionType,
    @ColumnInfo(name = "timestamp") var timestamp: Long?,
    @ColumnInfo(name = "textMessage") var textMessage: String?,
    @ColumnInfo(name = "byteMessage", typeAffinity = ColumnInfo.BLOB) var byteMessage: ByteArray?,
    @ColumnInfo(name = "messageSize") var sizeMessage: Long?,
    @ColumnInfo(name = "code") var code: Int?,
    @ColumnInfo(name = "reason") var reason: String?
) {

    val sizeMessageString: String
        get() {
            return FormatUtils.formatByteCount(sizeMessage ?: 0, true)
        }
}
