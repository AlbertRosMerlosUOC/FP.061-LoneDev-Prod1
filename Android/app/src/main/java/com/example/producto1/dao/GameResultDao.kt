package com.example.producto1.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.producto1.model.GameResult
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface GameResultDao {
    @Insert
    fun insertGameResult(gameResult: GameResult): Completable

    @Query("SELECT * FROM game_results WHERE playerId = :playerId")
    fun getResultsByPlayer(playerId: Int): Single<List<GameResult>>
}
