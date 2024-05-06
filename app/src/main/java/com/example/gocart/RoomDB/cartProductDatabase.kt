package com.example.gocart.RoomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [cartProducts::class] , version = 1 , exportSchema = false)
abstract class cartProductDatabase :RoomDatabase(){

    abstract fun cartProductsDao():CartProductDao

    companion object{
        @Volatile
        var INSTANCE:cartProductDatabase? = null

        fun getDatabaseInstance(context: Context):cartProductDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null)return tempInstance

            synchronized(this ){
                val roomDb = Room.databaseBuilder(context,cartProductDatabase::class.java,"CartProducts").allowMainThreadQueries().build()
                INSTANCE =roomDb
                return roomDb
            }
        }


    }


}