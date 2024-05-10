package com.example.gocart.Api

import com.example.gocart.Models.CheckStatus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path

interface ApiInterface {

    @GET("apis/pg-sandbox/pg/v1/status/{merchantId}/{transactionId}")
    suspend fun checkStatus(
        @HeaderMap header: Map<String , String>,
        @Path("merchandId")merchantId : String,
        @Path("transactionId")transactionId : String,
    ): Response<CheckStatus>

}