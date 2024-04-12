package com.chuckerteam.chucker.internal.ui.ws

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.chuckerteam.chucker.R

internal class WsTransactionPagerAdapter(context: Context, fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titles = arrayOf(
        context.getString(R.string.chucker_extended_message)
    )

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> WsTransactionMessageFragment()
        else -> throw IllegalArgumentException("no item")
    }

    override fun getCount(): Int = titles.size

    override fun getPageTitle(position: Int): CharSequence? = titles[position]
}
