package com.example.gocart.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gocart.RoomDB.cartProducts
import com.example.gocart.databinding.ItemViewCartProductBinding

class AdapterCartProducts:RecyclerView.Adapter<AdapterCartProducts.CartProductsViewholder>(){

    class CartProductsViewholder(val binding: ItemViewCartProductBinding): RecyclerView.ViewHolder(binding.root)

    val diffUtil = object :DiffUtil.ItemCallback<cartProducts>(){
        override fun areItemsTheSame(oldItem: cartProducts, newItem: cartProducts): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: cartProducts, newItem: cartProducts): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this , diffUtil)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterCartProducts.CartProductsViewholder {
        return CartProductsViewholder(ItemViewCartProductBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: AdapterCartProducts.CartProductsViewholder, position: Int
    ) {
        val product = differ.currentList[position]

        holder.binding.apply {
            Glide.with(holder.itemView).load(product.productImageUris).into(ivProductImage)
            productName.text = product.productTitle
            productQuantity.text = product.productQuantity
            productPrice.text = product.productPrice
            productCount.text = product.productCount.toString()


        }
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }
}