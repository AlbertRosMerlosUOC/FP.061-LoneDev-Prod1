package com.example.producto1.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.producto1.model.GameResult

@Dao
interface GameResultDao {
    @Query("SELECT * FROM game_results WHERE playerId = :playerId")
    fun getHistoryByPlayer(playerId: Int): List<GameResult>

    @Insert
    fun insertGame(game: GameResult)
}
