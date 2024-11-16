package com.example.producto1

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.producto1.databinding.ActivityMainBinding
import com.example.producto1.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private var jugadores: List<Player> = emptyList()
    private var jugadorActual: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar base de datos
        database = AppDatabase.getInstance(this)

        // Configurar acciones iniciales
        cargarJugadores()

        binding.botonAddPlayer.setOnClickListener {
            mostrarDialogoAñadirJugador()
        }
    }

    private fun cargarJugadores() {
        // Ejecutar la operación en un hilo secundario
        lifecycleScope.launch {
            jugadores = withContext(Dispatchers.IO) {
                database.playerDao().getAllPlayers()
            }

            configurarSpinner()
        }
    }

    private fun configurarSpinner() {
        // Convertir lista de jugadores a nombres
        val nombres = jugadores.map { it.name }

        // Configurar Spinner con los nombres
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, nombres)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        // Configurar selección de jugadores
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                jugadorActual = jugadores[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona un jugador
            }
        }

        binding.botonIniciarJuego.setOnClickListener {
            if (jugadorActual != null) {
                navegarPantallaJuego()
            } else {
                Toast.makeText(this, "Selecciona un jugador primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoAñadirJugador() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Crear nuevo jugador")

        val input = android.widget.EditText(this)
        input.hint = "Nombre del jugador"
        builder.setView(input)

        builder.setPositiveButton("Crear") { _, _ ->
            val nombre = input.text.toString().trim()
            if (nombre.isNotEmpty()) {
                lifecycleScope.launch {
                    val jugadorExistente = withContext(Dispatchers.IO) {
                        database.playerDao().findPlayerByName(nombre)
                    }

                    if (jugadorExistente == null) {
                        val nuevoJugador = Player(name = nombre, coins = 100)
                        withContext(Dispatchers.IO) {
                            database.playerDao().insertPlayer(nuevoJugador)
                        }
                        cargarJugadores() // Recargar jugadores
                        Toast.makeText(this@MainActivity, "Jugador creado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "El jugador ya existe", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun navegarPantallaJuego() {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("jugadorId", jugadorActual?.id)
        startActivity(intent)
    }
}
