package com.myfitness.app.domain

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.myfitness.app.data.dataBase.dao.TaskDao
import com.myfitness.app.data.dataBase.tables.Task
import com.myfitness.app.view.utils.STATUSCODES
import com.myfitness.app.view.utils.TaskCaseStates

class TaskUseCases(
    private val taskDao: TaskDao? = null ,
    private val dbRef : CollectionReference? = null
) {

    suspend fun getAllTasks(
        time : Long = 0L,
        returnFunc : (TaskCaseStates)-> Unit
    ){
         when{
            taskDao != null ->{
                 if(time == 0L){
                     returnFunc(
                         TaskCaseStates.Success(
                             data = taskDao.getAllTasks()
                         )
                     )
                }else{
                     returnFunc(
                         TaskCaseStates.Success(
                            data = taskDao.getAllTasksTime(time)
                         )
                     )
                }
            }

            dbRef != null ->{
                var list: List<Task>
                val ref= dbRef.get()
                ref.addOnSuccessListener {
                    list = it.documents.mapNotNull {doc->
                        if(doc.data?.keys.isNullOrEmpty()){
                            return@mapNotNull null
                        }
                       return@mapNotNull Task(
                           id = doc.data?.get(Task.ID)?.toString()?:"",
                           taskName = doc.data?.get(Task.TASK_NAME)?.toString()?:"",
                           status = doc.data?.get(Task.STATUS)?.toString().toBoolean(),
                           time = doc.data?.get(Task.TIME)?.toString()?.toLongOrNull()?:0L
                       )
                    }
                    returnFunc(
                        TaskCaseStates.Success(
                            data = list
                        )
                    )
                }.addOnFailureListener {
                    returnFunc(
                        TaskCaseStates.Failed(
                            status = STATUSCODES.DATA_FETCH_ERROR.status,
                            message = it.localizedMessage ?: it.message ?:""
                        )
                    )
                }

            }
            else->{
                returnFunc(
                    TaskCaseStates.Failed(
                        status = STATUSCODES.DATA_FETCH_ERROR.status,
                        message = "Internal Error"
                    )
                )
            }

        }

    }


    suspend fun saveTask(task : Task,returnFunc : (TaskCaseStates)-> Unit){
         when{
            taskDao != null ->{
                taskDao.addTask(task)

                returnFunc(
                    TaskCaseStates.Success(
                        data = "done"
                    )
                )

            }
            dbRef != null ->{
                val dataMap = hashMapOf(
                    Task.ID to task.id,
                    Task.TASK_NAME to task.taskName,
                    Task.STATUS to task.status,
                    Task.TIME to task.time.toString(),
                )

                dbRef.document(task.time.toString()).set(
                    dataMap,
                    SetOptions.merge()
                ).addOnSuccessListener {

                    returnFunc(
                        TaskCaseStates.Success(
                            data = "done"
                        )
                    )

                }.addOnFailureListener {

                    returnFunc(
                        TaskCaseStates.Failed(
                            status = STATUSCODES.DATA_WRITE_ERROR.status,
                            message = it.localizedMessage ?: it.message ?:""
                        )
                    )

                }
            }
            else ->{

                returnFunc(
                    TaskCaseStates.Failed(
                        status = STATUSCODES.DATA_WRITE_ERROR.status,
                        message = "Internal Error"
                    )
                )

            }
        }

    }

    suspend fun deleteTask(task: Task,returnFunc : (TaskCaseStates)-> Unit){
        when{
            taskDao != null ->{
                taskDao.deleteTask(task)
                val deletedTask = taskDao.getTask(task.id)

                if(deletedTask == null) {

                    returnFunc(
                        TaskCaseStates.Success(
                            status = STATUSCODES.SUCCESS.status,
                            data = "SUCCESS"
                        )
                    )

                }

                returnFunc(
                    TaskCaseStates.Failed(
                        status = STATUSCODES.DATA_DELETE_ERROR.status,
                        message = "Data Delete Error"
                    )
                )

            }
            dbRef != null ->{
                dbRef.document(task.time.toString()).delete().addOnSuccessListener {
                    returnFunc(
                        TaskCaseStates.Success(
                            status = STATUSCODES.SUCCESS.status,
                            data = "SUCCESS"
                        )
                    )
                }.addOnFailureListener {
                    returnFunc(
                        TaskCaseStates.Failed(
                            status = STATUSCODES.DATA_WRITE_ERROR.status,
                            message = it.localizedMessage ?: it.message ?:""
                        )
                    )
                }

            }
            else -> {
                returnFunc(
                    TaskCaseStates.Failed(
                        status = STATUSCODES.DATA_WRITE_ERROR.status,
                        message = "Internal Error"
                    )
                )
            }
        }

    }





}