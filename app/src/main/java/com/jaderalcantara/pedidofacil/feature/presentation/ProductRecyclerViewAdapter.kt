package com.jaderalcantara.pedidofacil.feature.presentation

import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.jaderalcantara.pedidofacil.R
import com.jaderalcantara.pedidofacil.feature.data.Product
import com.jaderalcantara.pedidofacil.infra.GlideApp
import java.text.NumberFormat


class ProductRecyclerViewAdapter(
    private val context: Context,
    private val vm: MainViewModel,
    private val values: List<Product>
) : RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        GlideApp.with(holder.image.context)
            .load("https://qa-m1-dr.abi-sandbox.net/media/catalog/product//-/R/-R002151.png")//Hardcodede url just for example
            .into(holder.image)
        item.name?.let {
            holder.name.text = it
        }
        item.quantity?.let {
            holder.unit.text = String.format(context.resources.getQuantityString(R.plurals.unit, it), it)
        }
        item.description?.let {
            holder.description.text = it
        }
        item.paidPrice?.let {
            holder.paidPrice.text = NumberFormat.getCurrencyInstance().format(it)
        }
        item.normalPrice?.let {
            holder.normalPrice.text = NumberFormat.getCurrencyInstance().format(it)
        }

        if(item.isPromo()){
            holder.paidPrice.setTextColor(context.getColor(R.color.green))
            holder.normalPrice.visibility = View.VISIBLE
            holder.normalPrice.paintFlags = STRIKE_THRU_TEXT_FLAG
        }else{
            holder.paidPrice.setTextColor(context.getColor(android.R.color.black))
            holder.normalPrice.visibility = View.GONE
        }

        if(vm.productIsAdded(item)){
            holder.quantity.text = vm.getQuantityFromCart(item).toString()
            holder.check.visibility = View.VISIBLE
            holder.add.visibility = View.INVISIBLE
        }else{
            holder.quantity.text = "0"
            holder.check.visibility = View.INVISIBLE
            holder.add.visibility = View.VISIBLE
        }

        holder.add.setOnClickListener {
            holder.add.visibility = View.INVISIBLE
            holder.check.visibility = View.VISIBLE
            item.id?.let {
                vm.addToCart(item, holder.quantity.text.toString().toInt())
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val minus: View = view.findViewById(R.id.minus)
        val plus: View = view.findViewById(R.id.plus)
        val quantity: TextView = view.findViewById(R.id.quantity)
        val name: TextView = view.findViewById(R.id.name)
        val unit: TextView = view.findViewById(R.id.unit)
        val description: TextView = view.findViewById(R.id.description)
        val paidPrice: TextView = view.findViewById(R.id.paid_price)
        val normalPrice: TextView = view.findViewById(R.id.normal_price)
        val add: Button = view.findViewById(R.id.add)
        val check: View = view.findViewById(R.id.check)

        init {
            minus.setOnClickListener {
                val amount = quantity.text.toString().toInt()
                if (amount > 0){
                    quantity.text = (amount - 1).toString()
                }
            }

            plus.setOnClickListener {
                val amount = quantity.text.toString().toInt()
                quantity.text = (amount + 1).toString()
            }
        }
    }
}