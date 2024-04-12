package com.chuckerteam.chucker.api

import android.content.Context

public class ExtendedChuckerCollector(
    context: Context,
    showNotification: Boolean = true,
    retentionPeriod: RetentionManager.Period = RetentionManager.Period.ONE_WEEK
) : ChuckerCollector(context, showNotification, retentionPeriod)
