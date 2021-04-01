package com.raindragon.chapter4_calculator.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.raindragon.chapter4_calculator.model.History

@Dao
interface HistoryDao {

    @Query("select * from history")
    fun getAll(): List<History>

    @Query("select * from history where result like :result")
    fun findByResults(result: String): List<History>

    @Query("select * from history where result like :result limit 1")
    fun findByResult(result: String): History

    @Insert
    fun insert(history: History)

    @Query("delete from history")
    fun deleteAll()
}