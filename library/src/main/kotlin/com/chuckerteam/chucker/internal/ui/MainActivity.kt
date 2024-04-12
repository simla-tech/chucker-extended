package com.chuckerteam.chucker.internal.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.api.Chucker
import com.chuckerteam.chucker.databinding.ChuckerActivityMainBinding
import com.chuckerteam.chucker.internal.data.entity.HttpTransaction
import com.chuckerteam.chucker.internal.data.model.DialogData
import com.chuckerteam.chucker.internal.support.HarUtils
import com.chuckerteam.chucker.internal.support.Logger
import com.chuckerteam.chucker.internal.support.NotificationHelper
import com.chuckerteam.chucker.internal.support.Sharable
import com.chuckerteam.chucker.internal.support.TransactionDetailsHarSharable
import com.chuckerteam.chucker.internal.support.TransactionListDetailsSharable
import com.chuckerteam.chucker.internal.support.shareAsFile
import com.chuckerteam.chucker.internal.support.showDialog
import com.chuckerteam.chucker.internal.ui.transaction.TransactionActivity
import com.chuckerteam.chucker.internal.ui.transaction.TransactionAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MainActivity : BaseChuckerActivity() {

    private lateinit var mainBinding: ChuckerActivityMainBinding

    private val applicationName: CharSequence
        get() = applicationInfo.loadLabel(packageManager)

    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermissionGranted: Boolean ->
        if (!isPermissionGranted) {
            showToast(
                applicationContext.getString(R.string.chucker_notifications_permission_not_granted),
                Toast.LENGTH_LONG
            )
            Logger.error("Notification permission denied. Can't show transactions info")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ChuckerActivityMainBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            handleNotificationsPermission()
        }

        with(mainBinding) {
            setContentView(root)
            setSupportActionBar(toolbar)
            toolbar.subtitle = applicationName
            viewPager.adapter = HomePageAdapter(this@MainActivity, supportFragmentManager)
            tabLayout.setupWithViewPager(viewPager)
            viewPager.addOnPageChangeListener(
                object : TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (position == HomePageAdapter.SCREEN_HTTP_INDEX) {
                            NotificationHelper(this@MainActivity).dismissTransactionsNotification()
                        } else {
                            NotificationHelper(this@MainActivity).dismissWsTransactionsNotification()
                        }
                    }
                }
            )
        }

        consumeIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        consumeIntent(intent)
    }

    /**
     * Scroll to the right tab.
     */
    private fun consumeIntent(intent: Intent) {
        if (intent.action != Intent.ACTION_MAIN) {
            // Get the screen to show, by default => HTTP
            val screenToShow = intent.getIntExtra(EXTRA_SCREEN, SCREEN_HTTP)
            mainBinding.viewPager.currentItem = if (screenToShow == SCREEN_HTTP) {
                HomePageAdapter.SCREEN_HTTP_INDEX
            } else {
                HomePageAdapter.SCREEN_WEB_SOCKET_INDEX
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun handleNotificationsPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                /* We have permission, all good */
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Snackbar.make(
                    mainBinding.root,
                    applicationContext.getString(R.string.chucker_notifications_permission_not_granted),
                    Snackbar.LENGTH_LONG
                ).setAction(applicationContext.getString(R.string.chucker_change)) {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        data = Uri.fromParts("package", packageName, null)
                    }.also { intent ->
                        startActivity(intent)
                    }
                }.show()
            }
            else -> {
                permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    companion object {
        const val EXTRA_SCREEN = "EXTRA_SCREEN"

        const val SCREEN_HTTP: Int = 1
        const val SCREEN_WEBSOCKET: Int = 2
    }
}
