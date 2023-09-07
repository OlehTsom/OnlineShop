package com.example.onlineshop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshop.data.Category
import com.example.onlineshop.data.Product
import com.example.onlineshop.data.SearchCategory
import com.example.onlineshop.databinding.SearchCategriesItemRvBinding

class SearchCategoriesAdapter : RecyclerView.Adapter<SearchCategoriesAdapter.CategoriesViewHolder>() {

    inner class CategoriesViewHolder(private val binding: SearchCategriesItemRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: SearchCategory) {
            binding.tvNameCategories.text = category.nameCategory
            Glide.with(itemView).load(category.image).into(binding.imgProductCategoriesSearch)
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<SearchCategory>(){
        override fun areItemsTheSame(oldItem: SearchCategory, newItem: SearchCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchCategory, newItem: SearchCategory): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            SearchCategriesItemRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = differ.currentList[position]

        holder.bind(category)

        holder.itemView.setOnClickListener {
            onClick?.invoke(category)
        }
    }

    var onClick : ((SearchCategory) -> Unit) ?= null
}