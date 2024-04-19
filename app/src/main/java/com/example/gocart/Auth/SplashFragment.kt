package com.example.gocart.Auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.gocart.R
import com.example.gocart.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

    private lateinit var binding : FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =FragmentSplashBinding.inflate(layoutInflater)


        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_signinFragment)
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