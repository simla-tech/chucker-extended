package com.chuckerteam.chucker.internal.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.internal.ui.transaction.TransactionListFragment
import com.chuckerteam.chucker.internal.ui.ws.WsTransactionListFragment
import java.lang.ref.WeakReference

internal class HomePageAdapter(context: Context, fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val context: WeakReference<Context> = WeakReference(context)

    override fun getItem(position: Int): Fragment = if (position == SCREEN_HTTP_INDEX) {
        TransactionListFragment.newInstance()
    } else {
        WsTransactionListFragment.newInstance()
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? =
        context.get()?.getString(
            if (position == SCREEN_HTTP_INDEX) {
                R.string.chucker_tab_http
            } else {
                R.string.chucker_tab_web_socket
            }
        )

    companion object {
        const val SCREEN_HTTP_INDEX = 0
        const val SCREEN_WEB_SOCKET_INDEX = 1
    }
}
