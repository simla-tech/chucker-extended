package com.chuckerteam.chucker.internal.ui.ws

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.databinding.ChuckerActivityTransactionBinding
import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import com.chuckerteam.chucker.internal.support.Sharable
import com.chuckerteam.chucker.internal.support.WsTransactionDetailsSharable
import com.chuckerteam.chucker.internal.support.shareAsFile
import com.chuckerteam.chucker.internal.support.shareAsUtf8Text
import com.chuckerteam.chucker.internal.ui.BaseChuckerActivity
import kotlinx.coroutines.launch

internal class WsTransactionActivity : BaseChuckerActivity() {

    private val viewModel: WsTransactionViewModel by viewModels {
        WsTransactionViewModelFactory(intent.getLongExtra(EXTRA_TRANSACTION_ID, 0))
    }

    private lateinit var transactionBinding: ChuckerActivityTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionBinding = ChuckerActivityTransactionBinding.inflate(layoutInflater)

        with(transactionBinding) {
            setContentView(root)
            setSupportActionBar(toolbar)
            setupViewPager(viewPager)
            tabLayout.setupWithViewPager(viewPager)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.transactionTitle.observe(
            this,
            Observer { transactionBinding.toolbarTitle.text = it }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chucker_ws_transaction, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.share_text -> shareTransactionAsText { transaction ->
            WsTransactionDetailsSharable(transaction)
        }
        R.id.share_file -> shareTransactionAsFile { transaction ->
            WsTransactionDetailsSharable(transaction)
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun shareTransactionAsText(block: (WsTransaction) -> Sharable): Boolean {
        val transaction = viewModel.transaction.value
        if (transaction == null) {
            showToast(getString(R.string.chucker_extended_transaction_not_ready))
            return true
        }
        val sharable = block(transaction)
        lifecycleScope.launch {
            val shareIntent = sharable.shareAsUtf8Text(
                activity = this@WsTransactionActivity,
                intentTitle = getString(R.string.chucker_share_transaction_title),
                intentSubject = getString(R.string.chucker_share_transaction_subject)
            )
            startActivity(shareIntent)
        }
        return true
    }

    private fun shareTransactionAsFile(block: (WsTransaction) -> Sharable): Boolean {
        val transaction = viewModel.transaction.value
        if (transaction == null) {
            showToast(getString(R.string.chucker_extended_transaction_not_ready))
            return true
        }

        val sharable = block(transaction)
        lifecycleScope.launch {
            val shareIntent = sharable.shareAsFile(
                activity = this@WsTransactionActivity,
                fileName = EXPORT_FILE_NAME,
                intentTitle = getString(R.string.chucker_share_transaction_title),
                intentSubject = getString(R.string.chucker_share_transaction_subject),
                clipDataLabel = "transaction"
            )
            if (shareIntent != null) {
                startActivity(shareIntent)
            }
        }
        return true
    }

    private fun setupViewPager(viewPager: ViewPager) {
        viewPager.adapter = WsTransactionPagerAdapter(this, supportFragmentManager)
        viewPager.addOnPageChangeListener(
            object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    selectedTabPosition = position
                }
            }
        )
        viewPager.currentItem = selectedTabPosition
    }

    companion object {
        private const val EXPORT_FILE_NAME = "ws_transaction.txt"
        private const val EXTRA_TRANSACTION_ID = "transaction_id"
        private var selectedTabPosition = 0

        fun start(context: Context, transactionId: Long) {
            val intent = Intent(context, WsTransactionActivity::class.java)
            intent.putExtra(EXTRA_TRANSACTION_ID, transactionId)
            context.startActivity(intent)
        }
    }
}
