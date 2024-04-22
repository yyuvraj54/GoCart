package com.example.gocart.Auth

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gocart.Activity.UsersMainActivity
import com.example.gocart.R
import com.example.gocart.Utils
import com.example.gocart.ViewModels.AuthViewModel
import com.example.gocart.databinding.FragmentSplashBinding
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var binding : FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =FragmentSplashBinding.inflate(layoutInflater)
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.isCurrentUser.collect{
                    if(it){
                        startActivity(Intent(requireActivity() , UsersMainActivity::class.java))
                        requireActivity().finish()
                    }
                    else{
                        findNavController().navigate(R.id.action_splashFragment_to_signinFragment)
                    }
                }
            }


        },3000)


        return binding.root
    }

//    private fun setStatusBarColor(){
//        activity?.window?.apply {
//            val statusBarColors  = ContextCompat.getColor(requireContext() , R.color.back)
//            statusBarColor = statusBarColors
//            if(Build.VERSION.SDK_INT >= SDK_INT >= Build.VERSION.M){
//                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            }
//        }
//    }

}