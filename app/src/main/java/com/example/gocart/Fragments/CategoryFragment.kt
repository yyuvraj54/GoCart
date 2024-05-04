package com.example.gocart.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gocart.Adapters.AdapterProduct
import com.example.gocart.Models.Product
import com.example.gocart.R
import com.example.gocart.ViewModels.UserViewModel
import com.example.gocart.databinding.FragmentCategoryBinding
import com.example.gocart.databinding.FragmentHomeBinding
import com.example.gocart.databinding.FragmentSearchBinding
import com.example.gocart.databinding.ItemViewProductBinding
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private val viewModel:UserViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    private  var category:String? =null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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
                adapterProduct = AdapterProduct()
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
}