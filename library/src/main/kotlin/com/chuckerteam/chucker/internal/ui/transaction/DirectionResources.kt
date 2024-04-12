package com.chuckerteam.chucker.internal.ui.transaction

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.chuckerteam.chucker.R

internal sealed class DirectionResources(@DrawableRes val icon: Int, @ColorRes val color: Int) {
    class Send : DirectionResources(
        R.drawable.chucker_extended_ic_upward_white24,
        R.color.chucker_extended_ws_outbound
    )

    class Receive : DirectionResources(
        R.drawable.chucker_extended_ic_downward_white24,
        R.color.chucker_extended_ws_inbound
    )

    class ReceiveError : DirectionResources(
        R.drawable.chucker_extended_ic_downward_white24,
        R.color.chucker_extended_ws_error
    )
}
