package com.example.onlineshop.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.onlineshop.R
import com.example.onlineshop.data.CartProduct
import com.example.onlineshop.databinding.BillingProductsRvItemBinding
import com.example.onlineshop.helper.getProductPrice

class BillingProductsAdapter : RecyclerView.Adapter<BillingProductsAdapter.BillingViewHolder>() {
    inner class BillingViewHolder(val binding : BillingProductsRvItemBinding) : ViewHolder(binding.root) {
        fun bind(billingProduct : CartProduct){
            binding.apply {
                Glide.with(itemView.context).load(billingProduct.product.images[0]).into(binding.imageCartProduct)
                tvProductCartName.text = billingProduct.product.name
                tvBillingProductAmount.text = billingProduct.amount.toString()
                tvCartProductSize.text = billingProduct.selectedSize

                tvProductCartPrice.text = itemView.context.getString(R.string.dolar) + billingProduct.product.price
                tvProductCartPrice.text = itemView.context.getString(R.string.dolar) + billingProduct.product.offerPercentage
                    .getProductPrice(billingProduct.product.price)

                imageCartProductColor.setImageDrawable(ColorDrawable(billingProduct.selectedColor ?: Color.TRANSPARENT))
                binding.tvCartProductSize.text = billingProduct.selectedSize ?: ""
                    .also { binding.imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingViewHolder {
        return BillingViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }


}