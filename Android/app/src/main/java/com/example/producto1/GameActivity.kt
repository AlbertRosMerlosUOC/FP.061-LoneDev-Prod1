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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar base de datos
        database = AppDatabase.getInstance(this)

        // Obtener jugador actual
        val jugadorId = intent.getIntExtra("jugadorId", -1)

        // Realizar la consulta en un hilo de fondo para evitar bloquear el hilo principal
        lifecycleScope.launch {
            jugadorActual = withContext(Dispatchers.IO) {
                database.playerDao().getAllPlayers().find { it.id == jugadorId }
            }

            if (jugadorActual != null) {
                actualizarMonedas()
            } else {
                // Si el jugador no se encuentra, finalizar la actividad
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
        // Actualiza las monedas dependiendo de si se ganó o se perdió
        if (symbol1 == symbol2 && symbol2 == symbol3) {
            jugadorActual?.coins = jugadorActual?.coins?.plus(100) ?: 0
        } else {
            jugadorActual?.coins = jugadorActual?.coins?.minus(10)?.coerceAtLeast(0) ?: 0
        }

        // Actualizar las monedas en la base de datos en segundo plano
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Asegúrate de que la base de datos sea actualizada en el hilo de fondo
                jugadorActual?.let { database.playerDao().updatePlayer(it) }
            }
            // Actualizar la interfaz en el hilo principal
            actualizarMonedas()
        }
    }

    private fun actualizarMonedas() {
        jugadorActual?.let {
            binding.coinsTextView.text = "Monedas: ${it.coins}"
        }
    }
}
