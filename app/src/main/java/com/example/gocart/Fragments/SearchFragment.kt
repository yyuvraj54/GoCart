package com.example.gocart.Fragments

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gocart.Adapters.AdapterProduct
import com.example.gocart.CartListner
import com.example.gocart.Models.Product
import com.example.gocart.R
import com.example.gocart.RoomDB.cartProducts
import com.example.gocart.Utils
import com.example.gocart.ViewModels.UserViewModel
import com.example.gocart.databinding.FragmentSearchBinding
import com.example.gocart.databinding.ItemViewProductBinding
import kotlinx.coroutines.launch
import java.lang.ClassCastException

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    val viewModel : UserViewModel by viewModels()
    private var cartListner : CartListner? = null
    private lateinit var adapterProduct: AdapterProduct
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)


        getAllProducts()
        backToHomeFragment()
        SearcProducts()
        return binding.root
    }

    private fun backToHomeFragment() {
        binding.tbOtpFrag.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun getAllProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllTheProduct().collect{

                if(it.isEmpty()){
                    binding.rvProduct.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
                else {
                    binding.rvProduct.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(
                    ::onAddButtonClicked,
                    ::onIncrementButtonClicked,
                    ::onDecrementButtonClicked
                )
                binding.rvProduct.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }

    }

    private fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llproductCount.visibility =View.VISIBLE

        var itemCount = productBinding.count.text.toString().toInt()
        itemCount++;
        productBinding.count.text=itemCount.toString()

        cartListner?.showCartLayout(1)


        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListner?.savingCartItems(1)
            saveProductRoomDb(product)
            viewModel.updateItemCount(product,itemCount)
        }
    }
    fun onIncrementButtonClicked(product:Product , productBinding:ItemViewProductBinding){
        var itemCountInc = productBinding.count.text.toString().toInt()
        itemCountInc++;

        if(product.productStock!!.toInt() + 1 > itemCountInc) {

            productBinding.count.text = itemCountInc.toString()

            cartListner?.showCartLayout(1)

            product.itemCount = itemCountInc
            lifecycleScope.launch {
                cartListner?.savingCartItems(1)
                saveProductRoomDb(product)
                viewModel.updateItemCount(product, itemCountInc)
            }
        }
        else{
            Utils.showToast(requireContext(), "No more stork for item remaining")
        }

    }
    fun onDecrementButtonClicked(product:Product , productBinding:ItemViewProductBinding){
        var itemCountDec = productBinding.count.text.toString().toInt()
        itemCountDec--;
        productBinding.count.text=itemCountDec.toString()


        product.itemCount = itemCountDec
        lifecycleScope.launch {
            cartListner?.savingCartItems(-1)
            saveProductRoomDb(product)
            viewModel.updateItemCount(product,itemCountDec)

        }
        if(itemCountDec>0){
            productBinding.count.text = itemCountDec.toString()

        }
        else{

            lifecycleScope.launch { viewModel.deleteCartProduct(product.productRandomId!!) }
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llproductCount.visibility = View.GONE
            productBinding.count.text = "0"

        }

        cartListner?.showCartLayout(-1)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is CartListner){
            cartListner = context
        }
        else{
            throw ClassCastException("Implement CartListner")
        }
    }
    private fun SearcProducts(){
        binding.searchEt.addTextChangedListener ( object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = s.toString().trim()
                Log.d(ContentValues.TAG, "onTextChanged: invoked")
                adapterProduct.getFilter().filter(query)

            }
            override fun afterTextChanged(p0: Editable?) {}
        } )
    }

    private fun saveProductRoomDb(product: Product) {
        val cardProduct = cartProducts(
            productId = product.productRandomId!!,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString() + product.productUnit.toString(),
            productPrice = "₹${product.productPrice}",
            productStock = product.productStock,
            productCategory =product.productCategory ,
            productCount = product.itemCount,
            adminUid = product.adminUid,
            productImageUris =product.productImageUris?.get(0)!!
        )

        lifecycleScope.launch {
            viewModel.insertCartProduct(cardProduct)
        }

    }
}