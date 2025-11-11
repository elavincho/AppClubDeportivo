package com.elavincho.appclubdeportivo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SocioAdapter(private val socios: List<Socio>) :
    RecyclerView.Adapter<SocioAdapter.SocioViewHolder>() {

    class SocioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtSocioNumero: TextView = itemView.findViewById(R.id.txtSocioNumero)
        val txtNombreCompleto: TextView = itemView.findViewById(R.id.txtNombreCompleto)
        val txtDocumento: TextView = itemView.findViewById(R.id.txtDocumento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio, parent, false)
        return SocioViewHolder(view)
    }

    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val socio = socios[position]

        holder.txtSocioNumero.text = "Socio Número: ${socio.id}"
        holder.txtNombreCompleto.text = "${socio.nombre} ${socio.apellido}"
        holder.txtDocumento.text = "${socio.tipoDoc}: ${socio.nroDoc}"
    }

    override fun getItemCount(): Int {
        return socios.size
    }
}