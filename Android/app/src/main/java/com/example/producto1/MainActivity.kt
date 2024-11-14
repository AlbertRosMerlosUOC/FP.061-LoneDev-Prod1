package com.example.producto1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.producto1.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val symbols = listOf(
        R.drawable.ic_reels_0,
        R.drawable.ic_reels_1,
        R.drawable.ic_reels_2,
        R.drawable.ic_reels_3,
        R.drawable.ic_reels_4,
        R.drawable.ic_reels_5,
        R.drawable.ic_reels_6
    )
    private var coins = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura el balance inicial de monedas
        binding.coinsTextView.text = "Balance: $coins"

        // Botón de "Girar"
        binding.spinButton.setOnClickListener {
            spinReels()
        }
    }

    private fun spinReels() {
        val spinDuration = 2000L // Duración del giro
        val delay = 100L // Tiempo entre cada cambio de imagen

        val handler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()

        handler.post(object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime

                // Generar símbolos aleatorios para cada carrete
                val symbol1 = symbols[Random.nextInt(symbols.size)]
                val symbol2 = symbols[Random.nextInt(symbols.size)]
                val symbol3 = symbols[Random.nextInt(symbols.size)]

                // Actualizar imágenes de los carretes
                binding.reel1.setImageResource(symbol1)
                binding.reel2.setImageResource(symbol2)
                binding.reel3.setImageResource(symbol3)

                // Continuar cambiando las imágenes hasta que se complete la duración del giro
                if (elapsedTime < spinDuration) {
                    handler.postDelayed(this, delay)
                } else {
                    // Verifica si el jugador ganó y actualiza las monedas
                    checkWin(symbol1, symbol2, symbol3)
                }
            }
        })
    }

    private fun checkWin(symbol1: Int, symbol2: Int, symbol3: Int) {
        if (symbol1 == symbol2 && symbol2 == symbol3) {
            // Jugador gana: sumar 100 monedas
            coins += 100
            binding.coinsTextView.text = "Balance: $coins"
        } else {
            // Restar monedas si el balance es mayor a 10
            if (coins >= 10) {
                coins -= 10
                binding.coinsTextView.text = "Balance: $coins"
            } else {
                binding.coinsTextView.text = "Balance: $coins (Insuficiente)"
            }
        }
    }
}
