package com.e.jetpackexample.persistance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.e.jetpackexample.models.AccountProperties


@Dao
interface AccountPropertiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountProperties: AccountProperties): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAndIgnore(accountProperties: AccountProperties):Long

    @Query("SELECT * FROM account_properties WHERE pk = :pk")
    fun searchByPk(pk:Int):AccountProperties?

    @Query("SELECT * FROM account_properties WHERE email = :email")
    suspend fun searchByEmail(email:Int):AccountProperties?
}