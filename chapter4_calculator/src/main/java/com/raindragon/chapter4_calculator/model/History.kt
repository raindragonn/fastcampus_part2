package com.raindragon.chapter4_calculator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class History(
    @PrimaryKey(autoGenerate = true)
    val uid: Int?,
    @ColumnInfo(name = "expression")
    val expression: String?,
    @ColumnInfo(name = "result")
    val result: String?
)
