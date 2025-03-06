package com.example.imctrack

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class LocationAdapter(
    private val context: Context,
    private val dataList: List<LocationData>
) : BaseAdapter() {

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Récupérer l'objet LocationData à la position donnée
        val locationData = dataList[position]

        // Si la vue est null, on la crée en utilisant un inflater
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_location, parent, false)

        // Initialiser les TextViews pour afficher les données
        val dateTextView: TextView = view.findViewById(R.id.item_date)
        val nameTextView: TextView = view.findViewById(R.id.item_name)
        val coordsTextView: TextView = view.findViewById(R.id.item_coords)

        // Remplir les TextViews avec les données de l'objet LocationData
        dateTextView.text = locationData.date
        nameTextView.text = locationData.name
        coordsTextView.text = "Lat: ${locationData.latitude}, Lon: ${locationData.longitude}"

        return view
    }
}
