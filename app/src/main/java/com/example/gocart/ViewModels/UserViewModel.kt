package com.example.gocart.ViewModels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.Display.Mode
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gocart.Models.Product
import com.example.gocart.RoomDB.CartProductDao
import com.example.gocart.RoomDB.cartProductDatabase
import com.example.gocart.RoomDB.cartProducts
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application): AndroidViewModel(application) {
    // Rooms ke liye alag se repository class banegi but because there are no multiple sources of Data Source we are not using Room (Although we have firebase and Room)

    val sharedPreferences : SharedPreferences = application.getSharedPreferences("My.Pref",MODE_PRIVATE)
    val cartProductDao : CartProductDao = cartProductDatabase.getDatabaseInstance(application).cartProductsDao()





    //Firebase Calls
    fun fetchAllTheProduct(): Flow<List<Product>> = callbackFlow {

        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products =ArrayList<Product>()

                for(product in snapshot.children){

                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}
    }
    fun getCategoryProduct(category: String):Flow<List<Product>> =  callbackFlow {
        val db  =FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${category}")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products =ArrayList<Product>()

                for(product in snapshot.children){

                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}
    }
    fun updateItemCount(product: Product,itemCount:Int){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productRandomId}").child("itemCount").setValue(itemCount)

    }


    //SharedPreferences Calls
    fun savingCartItemCount(itemCount: Int){
        sharedPreferences.edit().putInt("itemCount",itemCount).apply()
    }
    fun fetchTotalCartItemsCount(): MutableLiveData<Int> {
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreferences.getInt("itemCount",0)
        return totalItemCount
    }



    //RoomDB Calls
    suspend fun insertCartProduct(products:cartProducts){
        cartProductDao.insertCartProduct(products)
    }
    suspend fun updateCartProduct(products:cartProducts){
        cartProductDao.updateCartProduct(products)
    }
    suspend fun deleteCartProduct(productId:String){
        cartProductDao.deleteCartProduct(productId)
    }
    fun getAll(): LiveData<List<cartProducts>>{
        return cartProductDao.getAllCartProducts()
    }


}