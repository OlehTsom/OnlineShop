package com.example.onlineshop.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.onlineshop.R
import com.example.onlineshop.data.Order
import com.example.onlineshop.data.OrderStatus
import com.example.onlineshop.data.getOrderStatus
import com.example.onlineshop.databinding.OrderItemBinding

class AllOrdersAdapter : RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {
    inner class OrdersViewHolder(val binding : OrderItemBinding) : ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvOrderId.text = order.orderId.toString()
            binding.tvOrderDate.text = order.data
            val resources = itemView.resources

            val colorDrawable = when(getOrderStatus(order.orderStatus)){
                is OrderStatus.Ordered -> ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                is OrderStatus.Confirmed -> ColorDrawable(resources.getColor(R.color.g_green))
                is OrderStatus.Delivered -> ColorDrawable(resources.getColor(R.color.g_green))
                is OrderStatus.Shipped -> ColorDrawable(resources.getColor(R.color.g_green))
                is OrderStatus.Canceled -> ColorDrawable(resources.getColor(R.color.g_red))
                is OrderStatus.Returned -> ColorDrawable(resources.getColor(R.color.g_red))
            }

            binding.imageOrderState.setImageDrawable(colorDrawable)
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }

    }

    var onClick : ((Order) -> Unit) ?= null
}