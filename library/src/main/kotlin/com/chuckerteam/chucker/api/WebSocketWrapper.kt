package com.chuckerteam.chucker.api

import com.chuckerteam.chucker.internal.data.entity.WsTransaction.TransactionType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

public class WebSocketWrapper(
    private val okHttpClient: OkHttpClient,
    private val chuckerCollector: ExtendedChuckerCollector
) {

    public fun newWrappedWebSocket(
        socketUrl: String,
        webSocketListener: WebSocketListener
    ): WrappedWebSocket {
        val webSocket = okHttpClient.newWebSocket(
            Request.Builder().url(socketUrl).build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    chuckerCollector.onMessageReceived(
                        webSocket.startWsTransaction(TransactionType.RECEIVE_OPENED).apply {
                            timestamp = response.receivedResponseAtMillis
                        }
                    )
                    webSocketListener.onOpen(webSocket, response)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    if (FILTER_KEEP_ALIVE_MESSAGES && text.contains(KA_MESSAGE)) {
                        // filter keep-alive messages from the server to avoid log spam
                        return
                    }
                    chuckerCollector.onMessageReceived(
                        webSocket.startWsTransaction(TransactionType.RECEIVE_MESSAGE).apply {
                            textMessage = text
                            sizeMessage = text.toByteArray().size.toLong()
                        }
                    )
                    webSocketListener.onMessage(webSocket, text)
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    chuckerCollector.onMessageReceived(
                        webSocket.startWsTransaction(TransactionType.RECEIVE_MESSAGE).apply {
                            byteMessage = bytes.toByteArray()
                            sizeMessage = bytes.size.toLong()
                        }
                    )
                    webSocketListener.onMessage(webSocket, bytes)
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    chuckerCollector.onMessageReceived(
                        webSocket.startWsTransaction(TransactionType.RECEIVE_CLOSING).apply {
                            this.code = code
                            this.reason = reason
                        }
                    )
                    webSocketListener.onClosing(webSocket, code, reason)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    chuckerCollector.onMessageReceived(
                        webSocket.startWsTransaction(TransactionType.RECEIVE_CLOSED).apply {
                            this.code = code
                            this.reason = reason
                        }
                    )
                    webSocketListener.onClosed(webSocket, code, reason)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    chuckerCollector.onMessageReceived(
                        webSocket.startWsTransaction(TransactionType.RECEIVE_FAILURE).apply {
                            this.code = response?.code
                            this.reason = response?.message ?: t.localizedMessage
                        }
                    )
                    webSocketListener.onFailure(webSocket, t, response)
                }
            }
        )
        return WrappedWebSocket(webSocket, chuckerCollector)
    }

    private companion object {
        const val FILTER_KEEP_ALIVE_MESSAGES = true
        const val KA_MESSAGE = "{\"type\":\"ka\"}\n"
    }
}
