package com.example.gocart.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gocart.Adapters.AdapterCategory
import com.example.gocart.Constants
import com.example.gocart.Models.Category
import com.example.gocart.R
import com.example.gocart.ViewModels.UserViewModel
import com.example.gocart.databinding.FragmentHomeBinding
class HomeFragment : Fragment() {
    private lateinit var  binding : FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =FragmentHomeBinding.inflate(layoutInflater)


        setAllCategories()
        navigateToSearchFragment()
//        onCategoryIconClicked()
        get()
        return binding.root
    }


    private fun navigateToSearchFragment() {
        binding.searchitem.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }
    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()
        for(i in 0 until Constants.allProductsCategory.size){
            categoryList.add(Category(title = Constants.allProductsCategory[i], image = Constants.allProductsCategoryIcon[i]))
        }
        binding.rvcategory.adapter = AdapterCategory(categoryList , :: onCategoryIconClicked)

    }
    private fun onCategoryIconClicked(category: Category) {
        val bundle =Bundle()
        bundle.putString("category" , category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment,bundle)
    }

    private fun get(){
        viewModel.getAll().observe(viewLifecycleOwner){
            for(i in it){
                Log.d("ccc" , i.productTitle.toString())
                Log.d("ccc" , i.productTitle.toString())

            }
        }

    }


}