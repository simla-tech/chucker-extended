package com.chuckerteam.chucker.api

import android.content.Context
import android.net.Uri

/**
 * No-op implementation.
 */
@Suppress("UnusedPrivateMember", "UNUSED_PARAMETER")
public open class ChuckerCollector @JvmOverloads constructor(
    context: Context,
    public var showNotification: Boolean = true,
    retentionPeriod: RetentionManager.Period = RetentionManager.Period.ONE_WEEK
) {
    @Suppress("FunctionOnlyReturningConstant")
    public fun writeTransactions(
        context: Context,
        startTimestamp: Long?,
        exportFormat: ExportFormat = ExportFormat.LOG
    ): Uri? = null
}
