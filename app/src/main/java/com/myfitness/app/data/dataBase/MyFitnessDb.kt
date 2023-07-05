package com.myfitness.app.data.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myfitness.app.data.dataBase.dao.TaskDao
import com.myfitness.app.data.dataBase.tables.Task

@Database(
    entities = [(Task::class)],
    version = 1,
    exportSchema = false,
)
abstract class MyFitnessDb : RoomDatabase(){
    abstract fun taskDao() : TaskDao

companion object{
    @Volatile
    private var instanceDb : MyFitnessDb? = null

    /***
     * context : as Application Context
     */
    fun getInstance(
        context:Context
    ): MyFitnessDb {
        synchronized(this){
            if(instanceDb ==null){
                instanceDb = Room.databaseBuilder(
                    context,
                    MyFitnessDb::class.java,
                    "MyFitnessDb"
                ).fallbackToDestructiveMigration().build()

            }
            return instanceDb!!
        }
    }
}

}