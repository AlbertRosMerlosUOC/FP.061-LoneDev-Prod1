package com.example.producto1

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.producto1.dao.GameResultDao
import com.example.producto1.dao.PlayerDao
import com.example.producto1.model.GameResult
import com.example.producto1.model.Player

@Database(entities = [Player::class, GameResult::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun gameResultDao(): GameResultDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "slot_machine_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
