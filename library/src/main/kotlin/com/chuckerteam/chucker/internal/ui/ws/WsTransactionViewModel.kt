package com.chuckerteam.chucker.internal.ui.ws

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import com.chuckerteam.chucker.internal.data.repository.RepositoryProvider

internal class WsTransactionViewModel(transactionId: Long) : ViewModel() {

    val transactionTitle: LiveData<String> = RepositoryProvider.ws()
        .getTransaction(transactionId)
        .map { transaction -> "${transaction?.type}" }

    val transaction: LiveData<WsTransaction?> =
        RepositoryProvider.ws().getTransaction(transactionId)
}

internal class WsTransactionViewModelFactory(
    private val transactionId: Long = 0L
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == WsTransactionViewModel::class.java) { "Cannot create $modelClass" }
        @Suppress("UNCHECKED_CAST")
        return WsTransactionViewModel(transactionId) as T
    }
}
