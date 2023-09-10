package com.example.onlineshop.adapters

import android.graphics.Paint
import android.media.Image.Plane
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshop.R
import com.example.onlineshop.data.Product
import com.example.onlineshop.databinding.ProductRvItemBinding
import com.example.onlineshop.helper.getProductPrice

class BestProductsAdapter : RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(private val binding : ProductRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                tvName.text = product.name
                tvNewPrice.text = itemView.context.getString(R.string.dolar) + product.offerPercentage.getProductPrice(product.price)
                tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                if(product.offerPercentage == null || product.offerPercentage == 0f) {
                    tvPrice.paintFlags = 0
                    tvNewPrice.visibility = View.GONE
                }

                tvPrice.text = itemView.context.getString(R.string.dolar) + product.price
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        val binding = ProductRvItemBinding.inflate(
            LayoutInflater.from(parent.context)
        )
        return BestProductsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick : ((Product) -> Unit) ?= null
}