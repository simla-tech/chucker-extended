package com.chuckerteam.chucker.api

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
        return socket.send(text)
    }

    override fun send(bytes: ByteString): Boolean {
        return socket.send(bytes)
    }

    override fun close(code: Int, reason: String?): Boolean {
        return socket.close(code, reason)
    }

    override fun cancel() {
        socket.cancel()
    }
}
