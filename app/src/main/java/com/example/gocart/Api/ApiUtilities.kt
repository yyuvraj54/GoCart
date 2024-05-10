package com.example.gocart.Api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {
    val statusAPi :ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-preprod.phonepe.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}