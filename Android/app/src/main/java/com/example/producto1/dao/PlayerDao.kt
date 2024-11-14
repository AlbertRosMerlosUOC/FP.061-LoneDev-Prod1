package com.example.producto1.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.producto1.model.Player
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface PlayerDao {
    @Insert
    fun insertPlayer(player: Player): Completable

    @Query("SELECT * FROM players WHERE id = :id")
    fun getPlayer(id: Int): Single<Player>

    @Query("UPDATE players SET coins = :coins WHERE id = :id")
    fun updateCoins(id: Int, coins: Int): Completable
}
