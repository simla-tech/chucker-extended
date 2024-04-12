package com.chuckerteam.chucker.internal.data.repository

import androidx.lifecycle.LiveData
import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import com.chuckerteam.chucker.internal.data.entity.WsTransactionTuple

/**
 * Repository Interface representing all the operations that are needed to let Chucker work
 * with [WsTransaction] and [WsTransactionTuple]. Please use [WsTransactionDatabaseRepository] that
 * uses Room and SqLite to run those operations.
 */
internal interface WsTransactionRepository {

    suspend fun insertTransaction(transaction: WsTransaction)

    fun updateTransaction(transaction: WsTransaction): Int

    suspend fun deleteOldTransactions(threshold: Long)

    suspend fun deleteAllTransactions()

    fun getSortedTransactionTuples(): LiveData<List<WsTransactionTuple>>

    fun getFilteredTransactionTuples(filterQuery: String): LiveData<List<WsTransactionTuple>>

    fun getTransaction(transactionId: Long): LiveData<WsTransaction?>

    suspend fun getAllTransactions(): List<WsTransaction>
}
