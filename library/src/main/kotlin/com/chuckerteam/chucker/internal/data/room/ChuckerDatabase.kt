package com.chuckerteam.chucker.internal.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chuckerteam.chucker.internal.data.entity.HttpTransaction
import com.chuckerteam.chucker.internal.data.entity.WsTransaction

@Database(
    entities = [
        HttpTransaction::class,
        WsTransaction::class
    ], version = 10, exportSchema = true
)
internal abstract class ChuckerDatabase : RoomDatabase() {

    abstract fun transactionDao(): HttpTransactionDao
    abstract fun wsDao(): WsTransactionDao

    companion object {
        private const val DB_NAME = "chucker.db"

        fun create(applicationContext: Context): ChuckerDatabase {
            return Room.databaseBuilder(applicationContext, ChuckerDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
