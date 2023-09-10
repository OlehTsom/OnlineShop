package com.example.onlineshop.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshop.R
import com.example.onlineshop.data.CartProduct
import com.example.onlineshop.data.Product
import com.example.onlineshop.databinding.CartProductItemBinding
import com.example.onlineshop.databinding.SpecialRvItemBinding
import com.example.onlineshop.helper.getProductPrice

class  CartProductsAdapter : RecyclerView.Adapter<CartProductsAdapter.CardProductsViewHolder>() {

    inner class CardProductsViewHolder(val binding : CartProductItemBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(cartProduct: CartProduct){
            binding.tvProductCardName.text = cartProduct.product.name

            Glide.with(itemView).load(cartProduct.product.images[0]).into(binding.imageCardProduct)

            binding.tvProductCardPrice.text = itemView.context.getString(R.string.dolar) + cartProduct.product.price
            binding.tvProductCardPrice.text = itemView.context.getString(R.string.dolar) + cartProduct.product.offerPercentage
                    .getProductPrice(cartProduct.product.price)


            binding.tvCardProductAmount.text = cartProduct.amount.toString()

            binding.imageCardProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor ?: Color.TRANSPARENT))
            binding.tvCardProductSize.text = cartProduct.selectedSize ?: ""
                .also { binding.imageCardProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardProductsViewHolder {
        val binding = CartProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CardProductsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CardProductsViewHolder, position: Int) {
        val cardProduct = differ.currentList[position]
        holder.bind(cardProduct)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cardProduct)
        }

        holder.binding.butMines.setOnClickListener {
            onMinesClick?.invoke(cardProduct)
        }

        holder.binding.butPlus.setOnClickListener {
            onPlusClick?.invoke(cardProduct)
        }
    }

    fun updateList(newList: List<CartProduct>) {
        differ.submitList(newList)
    }

    var onProductClick : ((CartProduct) -> Unit) ?= null
    var onMinesClick : ((CartProduct) -> Unit) ?= null
    var onPlusClick : ((CartProduct) -> Unit) ?= null
}