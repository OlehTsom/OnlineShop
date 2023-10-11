package com.example.onlineshop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshop.databinding.ViewpagerImageItemBinding

class ViewPager2ImagesAdapter :
    RecyclerView.Adapter<ViewPager2ImagesAdapter.Viewpager2ImagesHolder>() {

    inner class Viewpager2ImagesHolder(val binding: ViewpagerImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            Glide.with(itemView).load(image).into(binding.imageProductsDetails)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewpager2ImagesHolder {
        return Viewpager2ImagesHolder(
            ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: Viewpager2ImagesHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }
}