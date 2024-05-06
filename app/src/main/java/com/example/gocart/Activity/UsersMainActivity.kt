package com.example.gocart.Activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.gocart.CartListner
import com.example.gocart.R
import com.example.gocart.ViewModels.UserViewModel
import com.example.gocart.databinding.ActivityUsersMainBinding

class UsersMainActivity : AppCompatActivity() ,CartListner{

    private lateinit var binding :ActivityUsersMainBinding
    private val viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getTotalItemCount()
        onCartClicked()

    }

    override fun showCartLayout(itemCount: Int) {
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount  = previousCount + itemCount;

        if(updatedCount > 0){
            binding.llcart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        }
        else{
            binding.llcart.visibility = View.GONE
            binding.tvNumberOfProductCount.text ="0"
        }
    }

    override fun savingCartItems(itemCount: Int) {
        viewModel.fetchTotalCartItemsCount().observe(this){
            viewModel.savingCartItemCount(it + itemCount)
        }

    }

    fun getTotalItemCount(){
        viewModel.fetchTotalCartItemsCount().observe(this){
            if(it > 0){
                binding.llcart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = it.toString()

            }
            else{
                binding.llcart.visibility = View.GONE
            }
        }
    }

    fun onCartClicked(){
        binding.ivShowingProductCart.setOnClickListener {
            binding.llcart
        }

    }


}