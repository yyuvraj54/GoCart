package com.example.gocart.ViewModels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gocart.Api.ApiUtilities
import com.example.gocart.Constants
import com.example.gocart.Models.Product
import com.example.gocart.RoomDB.CartProductDao
import com.example.gocart.RoomDB.cartProductDatabase
import com.example.gocart.RoomDB.cartProducts
import com.example.gocart.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application): AndroidViewModel(application) {
    // Rooms ke liye alag se repository class banegi but because there are no multiple sources of Data Source we are not using Room (Although we have firebase and Room)

    val sharedPreferences : SharedPreferences = application.getSharedPreferences("My.Pref",MODE_PRIVATE)
    val cartProductDao : CartProductDao = cartProductDatabase.getDatabaseInstance(application).cartProductsDao()


    val _paymentStatus = MutableStateFlow<Boolean>(false)
    val paymentStatus = _paymentStatus





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
                Log.d("fetch Data Fail", "onCancelled: "+ error.toString())
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
    fun saveUserAddress(address: String){
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getUserId()).child("userAddress").setValue(address)

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
    fun savingAddressStatus(){
        sharedPreferences.edit().putBoolean("addressStatus",true).apply()
    }
    fun getAddress(): MutableLiveData<Boolean>{
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("addressStatus",false)
        return status
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


    //Retrofit
    suspend fun CheckPayment(header: Map<String ,String>){
        val res = ApiUtilities.statusAPi.checkStatus(header,Constants.MERCHANTID , Constants.merchantTransactionId)

        if(res.body() != null && res.body()!!.success){
            _paymentStatus.value = true
        }
        else{
            _paymentStatus.value = false
        }

    }


}