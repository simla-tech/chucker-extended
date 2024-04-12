package com.chuckerteam.chucker.internal.support

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.app.JobIntentService
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

internal class ClearDatabaseService : JobIntentService() {
    private val scope = MainScope()

    override fun onHandleWork(intent: Intent) {
        RepositoryProvider.initialize(applicationContext)
        scope.launch {
            when (intent.getParcelableExtra<ClearAction>(EXTRA_ITEM_TO_CLEAR)) {
                is ClearAction.Transaction -> {
                    RepositoryProvider.transaction().deleteAllTransactions()
                    NotificationHelper.clearBuffer()
                    NotificationHelper(applicationContext).dismissTransactionsNotification()
                }

                is ClearAction.WsTransaction -> {
                    RepositoryProvider.ws().deleteAllTransactions()
                    NotificationHelper.clearWsBuffer()
                    NotificationHelper(applicationContext).dismissWsTransactionsNotification()
                }
                null -> Unit
            }
        }
    }

    companion object {
        private const val CLEAN_DATABASE_JOB_ID = 123321

        const val EXTRA_ITEM_TO_CLEAR = "EXTRA_ITEM_TO_CLEAR"

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, ClearDatabaseService::class.java, CLEAN_DATABASE_JOB_ID, work)
        }
    }


    sealed class ClearAction : Parcelable {
        @Parcelize
        object Transaction : ClearAction()

        @Parcelize
        object WsTransaction : ClearAction()
    }
}
