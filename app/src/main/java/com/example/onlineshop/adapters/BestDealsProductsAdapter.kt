package com.example.onlineshop.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshop.R
import com.example.onlineshop.data.Product
import com.example.onlineshop.databinding.BestDealsRvItemBinding
import com.example.onlineshop.helper.getProductPrice

class BestDealsProductsAdapter : RecyclerView.Adapter<BestDealsProductsAdapter.BestDealViewHolder>() {

    inner class BestDealViewHolder(private val binding : BestDealsRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                tvDealProductName.text = product.name
                tvNewPrice.text = itemView.context.getString(R.string.dolar) + product.offerPercentage.getProductPrice(product.price)
                tvOldPrice.text = itemView.context.getString(R.string.dolar) + product.price.toString()
                tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                Glide.with(itemView).load(product.images[0]).into(binding.imgBestDeal)
            }
        }
    }



    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealViewHolder {
        val binding = BestDealsRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BestDealViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick : ((Product) -> Unit) ?= null


}