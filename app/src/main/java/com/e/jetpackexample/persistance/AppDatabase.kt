package com.e.jetpackexample.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.e.jetpackexample.models.AccountProperties
import com.e.jetpackexample.models.AuthToken
import com.e.jetpackexample.models.BlogPost

@Database(entities = [AuthToken::class, AccountProperties::class, BlogPost::class], version = 1)
abstract class AppDatabase :RoomDatabase(){


    abstract fun getAuthTokenDao(): AuthTokenDao
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao
    abstract fun getBlogPostDao(): BlogPostDao

    companion object{
        const val DATABASE_NAME = "app_db"
    }
}