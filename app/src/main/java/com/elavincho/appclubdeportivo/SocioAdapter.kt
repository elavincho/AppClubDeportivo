package com.elavincho.appclubdeportivo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SocioAdapter(
    private val socios: List<Socio>,
    private val onMenuClickListener: (Socio, Int) -> Unit
) : RecyclerView.Adapter<SocioAdapter.SocioViewHolder>() {

    class SocioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtSocioNumero: TextView = itemView.findViewById(R.id.txtSocioNumero)
        val txtNombreCompleto: TextView = itemView.findViewById(R.id.txtNombreCompleto)
        val txtDocumento: TextView = itemView.findViewById(R.id.txtDocumento)
        val btnMenuSocio: ImageView = itemView.findViewById(R.id.btnMenuSocio)
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

        // Configurar el clic del menú
        holder.btnMenuSocio.setOnClickListener {
            onMenuClickListener(socio, position)
        }

        // Opcional: hacer que el item completo sea clickeable para ver datos
        holder.itemView.setOnClickListener {
            // Puedes abrir los datos directamente al hacer clic en el item
            // onMenuClickListener(socio, position)
        }
    }

    override fun getItemCount(): Int = socios.size
}