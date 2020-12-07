package com.jaderalcantara.marvel.feature.presentation.favorites

import android.content.Context
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jaderalcantara.marvel.R
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.infra.GlideApp
import com.jaderalcantara.marvel.infra.ImageHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.ArrayList

class FavoritesRecyclerViewAdapter(
    private val vm: FavoritesViewModel,
    private val values: ArrayList<CharacterResponse>,
    private val listener: OnListListener
) : RecyclerView.Adapter<FavoritesRecyclerViewAdapter.ViewHolder>(), KoinComponent {
    private val imageHelper: ImageHelper by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.character_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        item.thumbnail?.base64?.let {
            imageHelper.loadImageBase64(it, holder.image)
        }

        item.name?.let {
            holder.name.text = it
        }

        holder.fav.setImageResource(R.drawable.ic_baseline_star_24)

        holder.fav.setOnClickListener {
            item.id?.let {
                vm.removeFavorite(item)
            }
            notifyItemRemoved(values.indexOf(item))
            values.remove(item)

            if(values.isEmpty()){
                listener.onListIsEmpty()
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        val fav: ImageView = view.findViewById(R.id.fav)
    }

    interface OnListListener{
        fun onListIsEmpty()
    }
}