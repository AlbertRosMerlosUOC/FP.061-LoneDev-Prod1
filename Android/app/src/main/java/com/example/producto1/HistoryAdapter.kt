package com.example.producto1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.producto1.databinding.ItemHistoryBinding
import com.example.producto1.model.GameResult

class HistoryAdapter(private val partidas: List<GameResult>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partida = partidas[position]
        holder.binding.resultadoTextView.text = "Resultado: ${partida.result1}"
        holder.binding.fechaTextView.text = "Fecha: ${partida.date}"
    }

    override fun getItemCount(): Int = partidas.size
}
