package com.chuckerteam.chucker.api

import android.content.Context
import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import com.chuckerteam.chucker.internal.support.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

public class ExtendedChuckerCollector(
    context: Context,
    showNotification: Boolean = true,
    retentionPeriod: RetentionManager.Period = RetentionManager.Period.ONE_WEEK
) : ChuckerCollector(context, showNotification, retentionPeriod) {

    private val notificationHelper: NotificationHelper = NotificationHelper(context)

    /**
     * Call this method when you send WebSocket message.
     * @param transaction The WebSocket transaction sent
     */
    internal fun onWsMessageSent(transaction: WsTransaction) {
        saveMessage(transaction)
    }

    /**
     * Call this method when you received a message over WebSocket.
     * @param transaction Received transaction.
     */
    internal fun onMessageReceived(transaction: WsTransaction) {
        saveMessage(transaction)
    }

    private fun saveMessage(transaction: WsTransaction) {
        CoroutineScope(Dispatchers.IO).launch {
            RepositoryProvider.ws().insertTransaction(transaction)
            withContext(Dispatchers.Main) {
                if (showNotification) {
                    notificationHelper.show(transaction)
                }
                retentionManager.doMaintenance()
            }
        }
    }
}
