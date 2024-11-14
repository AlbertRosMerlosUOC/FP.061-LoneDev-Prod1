package com.example.producto1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerId: Int,
    val betAmount: Int,
    val winAmount: Int,
    val date: String
)
