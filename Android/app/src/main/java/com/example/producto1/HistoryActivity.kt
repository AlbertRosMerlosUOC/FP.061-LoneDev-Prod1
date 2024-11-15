package com.example.producto1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.producto1.databinding.ActivityHistoryBinding
import com.example.producto1.model.Player
import com.example.producto1.model.GameResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var database: AppDatabase
    private var jugadorActual: Player? = null
    private lateinit var gameResultList: List<GameResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar base de datos
        database = AppDatabase.getInstance(this)

        // Obtener jugador actual desde el Intent
        val jugadorId = intent.getIntExtra("jugadorId", -1)

        // Consultar el historial de partidas en un hilo de fondo
        lifecycleScope.launch {
            // Obtener el jugador y su historial de partidas en un hilo de fondo
            jugadorActual = withContext(Dispatchers.IO) {
                database.playerDao().getAllPlayers().find { it.id == jugadorId }
            }

            // Si el jugador existe, obtener el historial de partidas
            if (jugadorActual != null) {
                // Obtener historial de partidas
                gameResultList = withContext(Dispatchers.IO) {
                    database.gameResultDao().getHistoryByPlayer(jugadorId)
                }

                // Actualizar la UI con el historial de partidas
                actualizarHistorial()
            } else {
                // Si no se encuentra el jugador, finalizar la actividad
                finish()
            }
        }

        // Aquí puedes añadir otras interacciones con la UI, como navegar a otras actividades.
    }

    private fun actualizarHistorial() {
        // Actualizar el RecyclerView o cualquier componente de la UI con el historial de partidas
        val adapter = HistoryAdapter(gameResultList)
        binding.recyclerView.adapter = adapter
    }
}
