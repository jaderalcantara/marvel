package com.jaderalcantara.marvel.feature.presentation.all

import android.content.Context
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

class AllRecyclerViewAdapter(
    private val vm: AllViewModel,
    private var values: ArrayList<CharacterResponse>
) : RecyclerView.Adapter<AllRecyclerViewAdapter.ViewHolder>(), KoinComponent {
    private val imageHelper: ImageHelper by inject()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.character_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        imageHelper.loadImage(item.thumbnail.path + "." + item.thumbnail.extension, holder.image)

        item.name?.let {
            holder.name.text = it
        }

        if(item.isFavorite){
            holder.fav.setImageResource(R.drawable.ic_baseline_star_24)
        }else{
            holder.fav.setImageResource(R.drawable.ic_baseline_star_border_24)
        }

        holder.fav.setOnClickListener {
            item.id?.let {
                vm.favorite(item, item.isFavorite)
            }
            if(item.isFavorite){
                item.isFavorite = false
                holder.fav.setImageResource(R.drawable.ic_baseline_star_border_24)
            }else{
                item.isFavorite = true
                holder.fav.setImageResource(R.drawable.ic_baseline_star_24)
            }
        }

        holder.itemView.setOnClickListener {
            vm.itemSelected(item)
        }
    }

    override fun getItemCount(): Int = values.size

    fun setItems(items: List<CharacterResponse>) {
        values = items as ArrayList<CharacterResponse>
        notifyDataSetChanged()
    }

    fun newState(character: CharacterResponse?) {
        character?.let {
            val indexOf = values.indexOf(it)
            if( indexOf != -1){
                if(it.isFavorite != values[indexOf].isFavorite){
                    values[indexOf].isFavorite = it.isFavorite
                    notifyItemChanged(indexOf)
                }
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val name: TextView = view.findViewById(R.id.name)
        val fav: ImageView = view.findViewById(R.id.fav)
    }
}