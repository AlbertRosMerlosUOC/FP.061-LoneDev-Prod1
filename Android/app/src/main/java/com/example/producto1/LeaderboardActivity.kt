package com.example.producto1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.producto1.databinding.ActivityLeaderboardBinding

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar base de datos
        database = AppDatabase.getInstance(this)

        // Configurar RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        actualizarClasificacion()
    }

    private fun actualizarClasificacion() {
        // Obtener jugadores ordenados por monedas (de mayor a menor)
        val jugadoresOrdenados = database.playerDao().getAllPlayers().sortedByDescending { it.coins }

        // Asignar jugadores al adaptador
        val adapter = LeaderboardAdapter(jugadoresOrdenados)
        binding.recyclerView.adapter = adapter
    }
}
