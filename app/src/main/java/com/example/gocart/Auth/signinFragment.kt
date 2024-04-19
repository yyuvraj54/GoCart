package com.example.gocart.Auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.gocart.R
import com.example.gocart.Utils
import com.example.gocart.databinding.FragmentSigninBinding


class signinFragment : Fragment() {
    private lateinit var binding : FragmentSigninBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  =FragmentSigninBinding.inflate(layoutInflater)

        getUserNumber()
        onContinueButtonClick()
        return binding.root
    }

    private fun onContinueButtonClick() {
        binding.SignupBtn.setOnClickListener {
            val number =binding.PhoneNumber.text.toString()

            if(number.length != 10){
                Utils.showToast(requireContext(),"Please enter valid phone number")
            }
            else{
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signinFragment_to_optFragment,bundle)
            }

        }
    }

    private fun getUserNumber() {
        binding.PhoneNumber.addTextChangedListener ( object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(number: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val len = number?.length

                if(len==10){
                    binding.SignupBtn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
                }
                else{
                    binding.SignupBtn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.grey))
                }
            }

            override fun afterTextChanged(p0: Editable?) {}


        })
    }
}