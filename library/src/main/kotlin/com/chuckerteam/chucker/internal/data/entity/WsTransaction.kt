@file:Suppress("TooManyFunctions")

package com.chuckerteam.chucker.internal.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.chuckerteam.chucker.internal.support.FormatUtils
import java.util.*

/**
 * Represents WebSocket transaction. Instances of this classes
 * should be populated as soon as the library receives data from OkHttp.
 */
@Suppress("LongParameterList")
@Entity(tableName = "ws_transactions")
internal class WsTransaction(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "host") var host: String?,
    @ColumnInfo(name = "sslEnabled") var sslEnabled: Boolean?,
    @ColumnInfo(name = "type") @TypeConverters(TransactionTypeConverter::class) var type: TransactionType?,
    @ColumnInfo(name = "timestamp") var timestamp: Long?,
    @ColumnInfo(name = "textMessage") var textMessage: String?,
    @ColumnInfo(name = "byteMessage", typeAffinity = ColumnInfo.BLOB) var byteMessage: ByteArray?,
    @ColumnInfo(name = "messageSize") var sizeMessage: Long?,
    @ColumnInfo(name = "code") var code: Int?,
    @ColumnInfo(name = "reason") var reason: String?
) {

    @Ignore
    constructor() : this(
        host = null,
        sslEnabled = null,
        type = null,
        timestamp = null,
        textMessage = null,
        byteMessage = null,
        sizeMessage = null,
        code = null,
        reason = null
    )

    enum class TransactionType {
        RECEIVE_OPENED,
        RECEIVE_MESSAGE,
        RECEIVE_CLOSING,
        RECEIVE_CLOSED,
        RECEIVE_FAILURE,
        SEND_MESSAGE,
        SEND_CLOSE,
        SEND_CANCEL;

        fun isError(): Boolean = this == RECEIVE_FAILURE
    }

    val timestampString: String?
        get() = timestamp?.let { Date(it).toString() }

    val sizeMessageString: String
        get() {
            return FormatUtils.formatByteCount(sizeMessage ?: 0, true)
        }

    val notificationText: String
        get() {
            return when (type) {
                TransactionType.RECEIVE_MESSAGE,
                TransactionType.SEND_MESSAGE ->
                    "${type!!.name} ${FormatUtils.extractGqlRoot(textMessage, "") ?: textMessage}"
                else -> {
                    "${type?.name ?: ""} ${code ?: ""}"
                }
            }
        }

    // Not relying on 'equals' because comparison be long due to request and response sizes
    // and it would be unwise to do this every time 'equals' is called.
    @Suppress("ComplexMethod")
    fun hasTheSameContent(other: WsTransaction?): Boolean {
        if (this === other) return true
        if (other == null) return false

        return (id == other.id) &&
            (host == other.host) &&
            (sslEnabled == other.sslEnabled) &&
            (type == other.type) &&
            (timestamp == other.timestamp) &&
            (textMessage == other.textMessage) &&
            (byteMessage.contentEquals(other.byteMessage)) &&
            (sizeMessage == other.sizeMessage) &&
            (code == other.code) &&
            (reason == other.reason)
    }

    private class TransactionTypeConverter {

        @TypeConverter
        fun save(transactionType: TransactionType?): Int? = transactionType?.ordinal

        @TypeConverter
        fun load(typeOrdinal: Int?): TransactionType? =
            if (typeOrdinal != null) {
                TransactionType.values()[typeOrdinal]
            } else null
    }
}
