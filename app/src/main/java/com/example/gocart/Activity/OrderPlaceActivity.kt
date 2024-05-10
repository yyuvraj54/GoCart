package com.example.gocart.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gocart.Adapters.AdapterCartProducts
import com.example.gocart.Constants
import com.example.gocart.Utils
import com.example.gocart.ViewModels.UserViewModel
import com.example.gocart.databinding.ActivityOrderPlaceBinding
import com.example.gocart.databinding.AddressLayoutBinding
import com.phonepe.intent.sdk.api.B2BPGRequest
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


class OrderPlaceActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels()
    private lateinit var binding: ActivityOrderPlaceBinding
    private  lateinit var adapterCartProducts: AdapterCartProducts
    private lateinit var  b2BPGRequest : B2BPGRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()
        backToHome()
        onPlaceOrder()
        initializePhonePay()

    }



    private fun initializePhonePay() {
        val data =  JSONObject()
        PhonePe.init(this, PhonePeEnvironment.UAT,  Constants.MERCHANTID,"")
        data.put("merchantId" ,  Constants.MERCHANTID)
        data.put("merchantTransactionId" , Constants.merchantTransactionId)
        data.put("amount" , 200)
        data.put("mobileNumber" , 8368278705)
        data.put("callbackUrl" , "https://webhook.site/callback-url")

        val paymentInsrument = JSONObject()
        paymentInsrument.put("type" ,"UPI_INTENT" )
        paymentInsrument.put("targetApp" , "com.phonepe.simulator")

        data.put("paymentInstrument",paymentInsrument)

        val deviceContext =JSONObject()
        deviceContext.put("deviceOS", "ANDROID")
        data.put("deviceContext",deviceContext)

        val payloadBase64 = Base64.encodeToString(
            data.toString().toByteArray(Charset.defaultCharset()),Base64.NO_WRAP
        )

        val checksum = sha256(payloadBase64 + Constants.apiEndPoint + Constants.SALT_KEY) + "###1"
         b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checksum)
            .setUrl(Constants.apiEndPoint)
            .build()

    }

    val PhonePayView = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            checkStatus()
        }

    }

    private fun checkStatus() {
        val xVerify = sha256("/pg/v1/status/${Constants.MERCHANTID}/${Constants.merchantTransactionId}${Constants.SALT_KEY}")+"###1"
        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to xVerify,
            "X-MERCHANT-ID" to Constants.MERCHANTID
        )
        lifecycleScope.launch {
            viewModel.CheckPayment(headers)
            viewModel.paymentStatus.collect{status ->
                if(status){
                    Utils.showToast(this@OrderPlaceActivity ,  "Payment Success")
                    startActivity(Intent(this@OrderPlaceActivity ,UsersMainActivity::class.java))
                    finish()
                }
                else{
                    Utils.showToast(this@OrderPlaceActivity ,  "Payment Failed")
                }

            }
        }

    }

    private fun getPaymentView(){
        try{
            PhonePe.getImplicitIntent(this, b2BPGRequest , "com.phonepe.simulator")
                .let{
                    PhonePayView.launch(it)
                }
        }
        catch (e: PhonePeInitException){
            Utils.showToast(this, e.message.toString())
        }

    }
    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold(""){ str, it-> str + "%02x".format(it) }
    }

    private fun onPlaceOrder() {
        binding.placeOrderBtn.setOnClickListener {
            viewModel.getAddress().observe(this){
                if(it){
                    getPaymentView()
                }
                else{
                    val addressLayoutBinding =AddressLayoutBinding.inflate(LayoutInflater.from(this))
                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.addBtn.setOnClickListener {
                        saveAddress(alertDialog , addressLayoutBinding)
                    }
                }

            }
        }
    }

    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialog(this  , "Processing...")
        val userPinCode = addressLayoutBinding.PinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.phoneNum.text.toString()
        val userState = addressLayoutBinding.State.text.toString()
        val userDistrict = addressLayoutBinding.District.text.toString()
        val userAddress = addressLayoutBinding.Address.text.toString()


        val address = "$userPinCode , $userDistrict ($userState) , $userAddress $userPhoneNumber"

        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.savingAddressStatus()
        }
        Utils.showToast(this , "Address Saved")
        alertDialog.dismiss()
        Utils.hideDialog()
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