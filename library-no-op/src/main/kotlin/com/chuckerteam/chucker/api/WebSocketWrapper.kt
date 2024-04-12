package com.chuckerteam.chucker.api

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener

public class WebSocketWrapper(
    private val okHttpClient: OkHttpClient,
    private val chuckerCollector: ExtendedChuckerCollector
) {

    public fun newWrappedWebSocket(
        socketUrl: String,
        webSocketListener: WebSocketListener
    ): WrappedWebSocket {
        val webSocket = okHttpClient.newWebSocket(
            Request.Builder().url(socketUrl).build(), webSocketListener
        )
        return WrappedWebSocket(webSocket, chuckerCollector)
    }
}
