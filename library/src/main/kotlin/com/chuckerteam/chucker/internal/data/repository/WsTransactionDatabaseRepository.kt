package com.chuckerteam.chucker.internal.data.repository

import androidx.lifecycle.LiveData
import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import com.chuckerteam.chucker.internal.data.entity.WsTransactionTuple
import com.chuckerteam.chucker.internal.data.room.ChuckerDatabase
import com.chuckerteam.chucker.internal.support.distinctUntilChanged

internal class WsTransactionDatabaseRepository(private val database: ChuckerDatabase) :
    WsTransactionRepository {

    private val wsDao get() = database.wsDao()

    override fun getFilteredTransactionTuples(filterQuery: String): LiveData<List<WsTransactionTuple>> {
        return wsDao.getFilteredTuples("$filterQuery%", "%$filterQuery%")
    }

    override fun getTransaction(transactionId: Long): LiveData<WsTransaction?> {
        return wsDao.getById(transactionId)
            .distinctUntilChanged { old, new -> old?.hasTheSameContent(new) != false }
    }

    override fun getSortedTransactionTuples(): LiveData<List<WsTransactionTuple>> {
        return wsDao.getSortedTuples()
    }

    override suspend fun deleteAllTransactions() {
        wsDao.deleteAll()
    }

    override suspend fun insertTransaction(transaction: WsTransaction) {
        val id = wsDao.insert(transaction)
        transaction.id = id ?: 0
    }

    override fun updateTransaction(transaction: WsTransaction): Int {
        return wsDao.update(transaction)
    }

    override suspend fun deleteOldTransactions(threshold: Long) {
        wsDao.deleteBefore(threshold)
    }

    override suspend fun getAllTransactions(): List<WsTransaction> = wsDao.getAll()
}
