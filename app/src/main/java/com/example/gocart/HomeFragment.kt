package com.example.gocart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gocart.Adapters.AdapterCategory
import com.example.gocart.Models.Category
import com.example.gocart.databinding.FragmentHomeBinding
class HomeFragment : Fragment() {
    private lateinit var  binding : FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =FragmentHomeBinding.inflate(layoutInflater)


        setAllCategories()
        return binding.root
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()
        for(i in 0 until Constants.allProductsCategory.size){
            categoryList.add(Category(title = Constants.allProductsCategory[i], image = Constants.allProductsCategoryIcon[i]))
        }
        binding.rvcategory.adapter = AdapterCategory(categoryList)

    }

}