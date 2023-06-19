package com.myfitness.app.storage.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.myfitness.app.storage.tables.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun editTask(task: Task)

    @Query("SELECT * FROM task")
    suspend fun getAllTasks(): List<Task>

    @Delete
    suspend fun deleteTask(task: Task)

}