package com.myfitness.app.storage.tables

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity("task")
data class Task(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id")
    val id:String,

    @ColumnInfo("taskName")
    val taskName:String,

    @ColumnInfo("status")
    var status:Boolean
)