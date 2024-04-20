package com.example.gocart.ViewModels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.gocart.Models.Users
import com.example.gocart.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel:ViewModel() {


    private val _verificationId = MutableStateFlow<String?>(null)
    private val _otpSent = MutableStateFlow(false)
    val otpSent = _otpSent

    private val _isSignedInSuccessfully  = MutableStateFlow(false)
    val isSignedInSuccessfully =_isSignedInSuccessfully

    fun sendOTP(userNumber:String , activity: Activity){

        // ye callback h jo run hoga jab option wala code run hoga
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {

                _verificationId.value = verificationId
                _otpSent.value =true  // jo dialog open ho raha usko close karne ke liye ye  use kiya  in OTP Fragment


            }
        }

        // firebase mai https://firebase.google.com/docs/auth/android/phone-auth
        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(otp : String , userNumber: String , user:Users  ) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
        Utils.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {

                    FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(user.uid!!).setValue(user)

                    _isSignedInSuccessfully.value =true
                } else {
                    // Update UI
                }
            }
    }

}