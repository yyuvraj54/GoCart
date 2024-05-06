package com.example.gocart.Fragments

import android.content.Context
import android.os.Bundle
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
import com.example.gocart.databinding.FragmentCategoryBinding
import com.example.gocart.databinding.ItemViewProductBinding
import kotlinx.coroutines.launch
import java.lang.ClassCastException

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private val viewModel:UserViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    private  var category:String? =null

    private var cartListner : CartListner? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)

        getProductCategory()
        setToolBarTitle()
        backToHomeFragment()
        onSearchMenuClicked()
        fetchCategoryProduct()
        return binding.root

    }

    private fun onSearchMenuClicked() {
        binding.tbOtpFrag.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.searchMenu ->{
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }

                else -> {false}
            }

        }
    }

    private fun fetchCategoryProduct() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getCategoryProduct(category!!).collect {
                if(it.isEmpty()){
                    binding.rvProduct.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }
                else {
                    binding.rvProduct.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(::onAddButtonClicked, ::onIncrementButtonClicked , ::onDecrementButtonClicked)
                binding.rvProduct.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
//                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun setToolBarTitle() {
        binding.tbOtpFrag.title = category
    }
    private fun getProductCategory() {
        val bundle = arguments
        category = bundle?.getString("category")
    }
    private fun backToHomeFragment() {
        binding.tbOtpFrag.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
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