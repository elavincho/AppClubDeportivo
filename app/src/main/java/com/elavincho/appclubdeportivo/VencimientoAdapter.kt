package com.elavincho.appclubdeportivo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VencimientoAdapter(
    private val socios: List<Socio>,
    private val onSocioClickListener: (Socio) -> Unit
) : RecyclerView.Adapter<VencimientoAdapter.VencimientoViewHolder>() {

    class VencimientoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombreCompleto: TextView = itemView.findViewById(R.id.txtNombreCompleto)
        val txtSocioNumero: TextView = itemView.findViewById(R.id.txtSocioNumero)
        val txtDocumento: TextView = itemView.findViewById(R.id.txtDocumento)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VencimientoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vencimiento, parent, false)
        return VencimientoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VencimientoViewHolder, position: Int) {
        val socio = socios[position]

        holder.txtNombreCompleto.text = "${socio.nombre} ${socio.apellido}"
        holder.txtSocioNumero.text = "Socio N°: ${socio.id}"
        holder.txtDocumento.text = "${socio.tipoDoc}: ${socio.nroDoc}"
        holder.txtEstado.text = "VENCE HOY"

        holder.itemView.setOnClickListener {
            onSocioClickListener(socio)
        }
    }

    override fun getItemCount(): Int = socios.size
}