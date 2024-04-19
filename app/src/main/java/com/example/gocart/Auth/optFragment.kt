package com.example.gocart.Auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gocart.R
import com.example.gocart.databinding.FragmentOptBinding


class optFragment : Fragment() {
    private  lateinit var binding: FragmentOptBinding
    private lateinit var userNumber : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOptBinding.inflate(layoutInflater)

        getUserNumber()




        return binding.root
    }

    private fun getUserNumber() {
        var bundle = arguments
        userNumber = bundle?.getString("number").toString()
        binding.num.text= "+91 $userNumber"

    }

}