package com.example.gocart.Activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.gocart.Adapters.AdapterCartProducts
import com.example.gocart.R
import com.example.gocart.RoomDB.cartProducts
import com.example.gocart.ViewModels.UserViewModel
import com.example.gocart.databinding.ActivityOrderPlaceBinding

class OrderPlaceActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()
    private lateinit var binding: ActivityOrderPlaceBinding
    private  lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()
        backToHome()
        onPlaceOrder()

    }

    private fun onPlaceOrder() {
        binding.placeOrderBtn.setOnClickListener {
            viewModel.getAddress().observe(this){
                if(it){

                }
                else{

                }

            }
        }
    }

    private fun getAllCartProducts(){
        viewModel.getAll().observe(this){
            adapterCartProducts =AdapterCartProducts()
            binding.rvProductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(it)

            var totalPrice = 0;

            for(products in it){
                val price  =products.productPrice?.substring(1)?.toInt()
                val itemCount = products.productCount!!
                totalPrice +=(price?.times(itemCount)!!)
            }


            binding.subTotalPrice.text  = totalPrice.toString()

            if(totalPrice < 200){
                binding.DiliveryPrice.text = "₹20"
                totalPrice += 15
            }

            binding.TotalPrice.text = totalPrice.toString()


        }

    }
    private fun backToHome() {
        binding.tbOtpFrag.setNavigationOnClickListener {
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }



}