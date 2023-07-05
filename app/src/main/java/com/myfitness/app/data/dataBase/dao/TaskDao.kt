package com.myfitness.app.data.dataBase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.myfitness.app.data.dataBase.tables.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun editTask(task: Task)

    @Query("SELECT * FROM task")
    suspend fun getAllTasks(): List<Task>


    @Query("SELECT * FROM task where id = :id  ")
    suspend fun getTask(id: String):Task?
    @Query("SELECT * FROM task where time = :time")
    suspend fun getAllTasksTime(time:Long): List<Task>

    @Delete
    suspend fun deleteTask(task: Task)

}