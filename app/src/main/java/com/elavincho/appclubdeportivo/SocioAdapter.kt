package com.elavincho.appclubdeportivo

import android.content.Intent
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

        // Hacer el item clickeable - navegar a DetalleSocioActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetalleSocioActivity::class.java)
            intent.putExtra("socio_id", socio.id)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return socios.size
    }
}