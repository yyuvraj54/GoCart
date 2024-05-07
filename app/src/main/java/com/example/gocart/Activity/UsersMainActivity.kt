package com.example.gocart.Activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View

import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.gocart.Adapters.AdapterCartProducts
import com.example.gocart.CartListner
import com.example.gocart.R
import com.example.gocart.RoomDB.cartProducts
import com.example.gocart.ViewModels.UserViewModel
import com.example.gocart.databinding.ActivityUsersMainBinding
import com.example.gocart.databinding.BottomSheetCartProductsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class UsersMainActivity : AppCompatActivity() ,CartListner{

    private lateinit var binding :ActivityUsersMainBinding
    private val viewModel: UserViewModel by viewModels()

    private  lateinit var adapterCartProducts:  AdapterCartProducts
    private  lateinit var  cartProductsList : List<cartProducts>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllCartProducts()
        getTotalItemCount()
        onCartClicked()

    }



    private fun onNextButtonClicked(){
        binding.btnext.setOnClickListener {
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
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
        binding.llcart.setOnClickListener {

            val bsCartProductBinding = BottomSheetCartProductsBinding.inflate(LayoutInflater.from(this))


            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductBinding.root)

            bsCartProductBinding.tvNumberOfProductCount.text = binding.tvNumberOfProductCount.text
            adapterCartProducts = AdapterCartProducts()
            bsCartProductBinding.rvProductItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductsList)
            bs.show()

        }

    }

    private fun getAllCartProducts(){
        viewModel.getAll().observe(this){
                cartProductsList =it
        }

    }

}
