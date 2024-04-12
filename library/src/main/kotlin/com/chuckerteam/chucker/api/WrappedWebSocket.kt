package com.chuckerteam.chucker.api

import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import com.chuckerteam.chucker.internal.data.entity.WsTransaction.TransactionType
import okhttp3.Request
import okhttp3.WebSocket
import okio.ByteString

public class WrappedWebSocket(
    private val socket: WebSocket,
    private val chuckerCollector: ExtendedChuckerCollector
) : WebSocket {

    override fun request(): Request {
        return socket.request()
    }

    override fun queueSize(): Long {
        return socket.queueSize()
    }

    override fun send(text: String): Boolean {
        chuckerCollector.onWsMessageSent(
            socket.startWsTransaction(TransactionType.SEND_MESSAGE).apply {
                textMessage = text
                sizeMessage = text.toByteArray().size.toLong()
            }
        )
        return socket.send(text)
    }

    override fun send(bytes: ByteString): Boolean {
        chuckerCollector.onWsMessageSent(
            socket.startWsTransaction(TransactionType.SEND_MESSAGE).apply {
                byteMessage = bytes.toByteArray()
                sizeMessage = bytes.size.toLong()
            }
        )
        return socket.send(bytes)
    }

    override fun close(code: Int, reason: String?): Boolean {
        chuckerCollector.onWsMessageSent(
            socket.startWsTransaction(TransactionType.SEND_CLOSE).apply {
                this.code = code
                this.reason = reason
            }
        )
        return socket.close(code, reason)
    }

    override fun cancel() {
        chuckerCollector.onWsMessageSent(socket.startWsTransaction(TransactionType.SEND_CANCEL))
        socket.cancel()
    }
}

internal fun WebSocket.startWsTransaction(type: TransactionType): WsTransaction {
    return WsTransaction().apply {
        host = request().url.host
        sslEnabled = request().isHttps
        this.type = type
        timestamp = System.currentTimeMillis()
    }
}
