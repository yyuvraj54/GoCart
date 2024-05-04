package com.example.gocart.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.gocart.FilteringProducts
import com.example.gocart.databinding.ItemViewProductBinding
import com.example.gocart.Models.Product


class AdapterProduct() :RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(),Filterable {
    class ProductViewHolder(val binding: ItemViewProductBinding ): RecyclerView.ViewHolder(binding.root){}

    /// check this diffUtils this is for recycle view list of images // agar koi image frr se load ho rahi h to
    val diffutil = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId == newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffutil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemViewProductBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product =differ.currentList[position]


        holder.binding.apply {
            val imageList = ArrayList<SlideModel>()

            val productImage = product.productImageUris

            if(!productImage.isNullOrEmpty()){
                for (i in 0 until productImage?.size!!) {
                    imageList.add(SlideModel(product.productImageUris!![i].toString()))
                }
                ivImageSlider.setImageList(imageList)
            }

            tvProductTitle.text = product.productTitle

            val quantity = product.productQuantity.toString() + product.productUnit
            tvProductQuantity.text = quantity

            tvProductPrice.text = "₹"+product.productPrice
        }

        holder.itemView.setOnClickListener {
//            onEditbuttonClicked(product)
        }




    }


    val filter : FilteringProducts? = null
    var originalList= ArrayList<Product>()
    override fun getFilter(): Filter {
        if(filter == null) return FilteringProducts(this,originalList)
        return filter

    }


}