package com.elavincho.appclubdeportivo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComprobanteAdapter(
    private val comprobantes: List<ComprobantePago>,
    private val onComprobanteClickListener: (ComprobantePago) -> Unit
) : RecyclerView.Adapter<ComprobanteAdapter.ComprobanteViewHolder>() {

    class ComprobanteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtActividad: TextView = itemView.findViewById(R.id.txtActividad)
        val txtImporte: TextView = itemView.findViewById(R.id.txtImporte)
        val txtMetodoPago: TextView = itemView.findViewById(R.id.txtMetodoPago)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComprobanteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comprobante, parent, false)
        return ComprobanteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComprobanteViewHolder, position: Int) {
        val comprobante = comprobantes[position]

        holder.txtFecha.text = comprobante.fecha
        holder.txtActividad.text = comprobante.actividad
        holder.txtImporte.text = comprobante.importe
        holder.txtMetodoPago.text = comprobante.metodoPago

        holder.itemView.setOnClickListener {
            onComprobanteClickListener(comprobante)
        }
    }

    override fun getItemCount(): Int = comprobantes.size
}