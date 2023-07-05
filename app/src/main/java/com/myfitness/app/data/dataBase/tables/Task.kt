package com.myfitness.app.data.dataBase.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.myfitness.app.data.dataBase.tables.Task.Companion.TASK
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Entity(TASK)
data class Task(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(ID)
    val id:String,

    @ColumnInfo(TASK_NAME)
    val taskName:String,

    @ColumnInfo(STATUS)
    var status:Boolean,

    @ColumnInfo(TIME)
    var time:Long
){
    companion object{
        const val TASK = "task"
        const val ID = "id"
        const val TASK_NAME = "taskName"
        const val STATUS = "status"
        const val TIME = "time"
    }

    fun getFormattedTime():String{
        val cal = Calendar.getInstance()
        cal.timeInMillis = time

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return sdf.format(cal.time)
    }
}