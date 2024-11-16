package com.example.producto1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.producto1.databinding.ActivityGameBinding
import com.example.producto1.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var database: AppDatabase
    private var jugadorActual: Player? = null
    private val symbols = listOf(
        R.drawable.ic_reels_0,
        R.drawable.ic_reels_1,
        R.drawable.ic_reels_2,
        R.drawable.ic_reels_3,
        R.drawable.ic_reels_4,
        R.drawable.ic_reels_5,
        R.drawable.ic_reels_6
    )
    private val symbolNames = listOf(
        "s0",
        "s1",
        "s2",
        "s3",
        "s4",
        "s5",
        "s6"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getInstance(this)

        val jugadorId = intent.getIntExtra("jugadorId", -1)

        lifecycleScope.launch {
            jugadorActual = withContext(Dispatchers.IO) {
                database.playerDao().getAllPlayers().find { it.id == jugadorId }
            }

            if (jugadorActual != null) {
                actualizarMonedas()
            } else {
                finish()
            }
        }

        binding.spinButton.setOnClickListener {
            spinReels()
        }

        binding.changeUserButton.setOnClickListener {
            finish()
        }

        binding.leaderboardButton.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        binding.historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("jugadorId", jugadorId)
            startActivity(intent)
        }
    }

    private fun spinReels() {
        val spinDuration = 2000L
        val delay = 50L
        val handler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()

        handler.post(object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                val symbol1 = symbols[Random.nextInt(symbols.size)]
                val symbol2 = symbols[Random.nextInt(symbols.size)]
                val symbol3 = symbols[Random.nextInt(symbols.size)]

                binding.reel1.setImageResource(symbol1)
                binding.reel2.setImageResource(symbol2)
                binding.reel3.setImageResource(symbol3)

                if (elapsedTime < spinDuration) {
                    handler.postDelayed(this, delay)
                } else {
                    checkWin(symbol1, symbol2, symbol3)
                }
            }
        })
    }

    private fun checkWin(symbol1: Int, symbol2: Int, symbol3: Int) {
        val symbol1Index = symbols.indexOf(symbol1)
        val symbol2Index = symbols.indexOf(symbol2)
        val symbol3Index = symbols.indexOf(symbol3)

        val symbol1Name = if (symbol1Index != -1) symbolNames[symbol1Index] else "-1"
        val symbol2Name = if (symbol2Index != -1) symbolNames[symbol2Index] else "-1"
        val symbol3Name = if (symbol3Index != -1) symbolNames[symbol3Index] else "-1"

        if (symbol1Name == "s0" && symbol2Name == "s0" && symbol3Name == "s0") {
            jugadorActual?.coins = jugadorActual?.coins?.plus(500) ?: 0
        }
        else if (symbol1Name == "s6" && symbol2Name == "s6" && symbol3Name == "s6") {
            jugadorActual?.coins = jugadorActual?.coins?.minus(100)?.coerceAtLeast(0) ?: 0
        }
        else if (symbol1Name == symbol2Name && symbol2Name == symbol3Name && symbol1Name != "s0" && symbol1Name != "s6") {
            jugadorActual?.coins = jugadorActual?.coins?.plus(100) ?: 0
        }
        else if ((symbol1Name == symbol2Name && symbol1Name != "s6" && symbol2Name != "s6") ||
            (symbol2Name == symbol3Name && symbol2Name != "s6" && symbol3Name != "s6") ||
            (symbol1Name == symbol3Name && symbol1Name != "s6" && symbol3Name != "s6")) {
            jugadorActual?.coins = jugadorActual?.coins?.plus(20) ?: 0
        }
        else {
            jugadorActual?.coins = jugadorActual?.coins?.minus(10)?.coerceAtLeast(0) ?: 0
        }

        if (jugadorActual?.coins == 0) {
            showDeletePlayerDialog()
        } else {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    jugadorActual?.let { database.playerDao().updatePlayer(it) }
                }
                actualizarMonedas()
            }
        }
    }

    private fun actualizarMonedas() {
        jugadorActual?.let {
            binding.coinsTextView.text = "Monedas: ${it.coins}"
        }
    }

    private fun showDeletePlayerDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("Tus monedas han llegado a 0. El jugador será eliminado.")
            .setPositiveButton("Seguir") { dialog, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        jugadorActual?.let {
                            if (it.id != 0) {
                                database.playerDao().deletePlayer(it)
                            }
                        }
                    }
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}
