package com.example.onlineshop.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.onlineshop.R
import com.example.onlineshop.data.Product
import com.example.onlineshop.databinding.BestDealsRvItemBinding
import com.example.onlineshop.fragments.shoping.ProductClickListener
import com.example.onlineshop.helper.getProductPrice

class ProductAdapterAutoCompleteText(
    private val context: Context,
    private val clickListener: ProductClickListener
) : ArrayAdapter<Product>(context, 0, mutableListOf()) {

    fun updateData(newData: List<Product>) {
        clear()
        addAll(newData)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val product = getItem(position) ?: return convertView ?: View(context)


        val binding = BestDealsRvItemBinding.inflate(LayoutInflater.from(context), parent, false)

        Glide.with(context).load(product.images[0]).into(binding.imgBestDeal)
        binding.tvDealProductName.text = product.name


        if (product.offerPercentage == null || product.offerPercentage == 0f) {
            binding.tvOldPrice.text = context.getString(R.string.dolar) + product.price
            binding.tvOldPrice.paintFlags = 0
            binding.tvNewPrice.visibility = View.GONE
        } else {
            binding.tvOldPrice.text = context.getString(R.string.dolar) + product.price
            binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvNewPrice.text =
                context.getString(R.string.dolar) + product.offerPercentage.getProductPrice(product.price)

        }

        binding.root.setOnClickListener {
            clickListener.onProductClick(product)
        }

        return binding.root
    }
}