package com.example.gocart.RoomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProduct(product:cartProducts)

    @Update
    fun updateCartProduct(product:cartProducts)

    @Query("SELECT * FROM cartProducts")
    fun getAllCartProducts() : LiveData<List<cartProducts>>

    @Query("DELETE FROM CartProducts WHERE productId = :productId")
    fun deleteCartProduct(productId:String)

}