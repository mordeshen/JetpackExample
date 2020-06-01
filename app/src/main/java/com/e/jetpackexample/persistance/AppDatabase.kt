package com.e.jetpackexample.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.e.jetpackexample.models.AccountProperties
import com.e.jetpackexample.models.AuthToken

@Database(entities = [AuthToken::class, AccountProperties::class], version = 1)
abstract class AppDatabase :RoomDatabase(){

    abstract fun getAuthTokenDao(): AuthTokenDao
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    companion object{
        const val DATABASE_NAME = "app_db"
    }
}