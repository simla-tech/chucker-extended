package com.chuckerteam.chucker.internal.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import com.chuckerteam.chucker.internal.data.entity.WsTransactionTuple

@Dao
internal interface WsTransactionDao {

    @Query(
        "SELECT id, host, sslEnabled, type, timestamp, textMessage, byteMessage, messageSize, code, reason" +
            " FROM ws_transactions ORDER BY timestamp DESC"
    )
    fun getSortedTuples(): LiveData<List<WsTransactionTuple>>

    @Query(
        "SELECT id, host, sslEnabled, type, timestamp, textMessage, byteMessage, messageSize, code, reason" +
            " FROM ws_transactions WHERE type LIKE :filterType OR textMessage LIKE :filterMessage" +
            " ORDER BY timestamp DESC"
    )
    fun getFilteredTuples(
        filterType: String,
        filterMessage: String
    ): LiveData<List<WsTransactionTuple>>

    @Insert
    suspend fun insert(transaction: WsTransaction): Long?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(transaction: WsTransaction): Int

    @Query("DELETE FROM ws_transactions")
    suspend fun deleteAll()

    @Query("SELECT * FROM ws_transactions WHERE id = :id")
    fun getById(id: Long): LiveData<WsTransaction?>

    @Query("DELETE FROM ws_transactions WHERE timestamp <= :threshold")
    suspend fun deleteBefore(threshold: Long)

    @Query("SELECT * FROM ws_transactions")
    suspend fun getAll(): List<WsTransaction>
}
