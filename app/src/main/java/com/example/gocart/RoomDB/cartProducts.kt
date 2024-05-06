package com.example.gocart.RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CartProducts")
data class cartProducts(

    @PrimaryKey
    var productId: String = "random", // can't apply null check

    var productTitle: String ? = null,
    var productQuantity: String ? = null,
    var productPrice: String ?= null,
    var productStock: Int ? = null,
    var productCategory: String ? = null,
    var productCount: Int ? = null,
    var adminUid: String ? = null,
    var productImageUris: String? =null
)