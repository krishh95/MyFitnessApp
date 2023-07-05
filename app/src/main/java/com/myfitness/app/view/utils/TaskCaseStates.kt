package com.myfitness.app.view.utils

sealed class TaskCaseStates{

    class Success<T>(
        val status : String = STATUSCODES.SUCCESS.status,
        val message :String = "Success",
        val data : T
    ) : TaskCaseStates()

    class Failed(
        val status : String = STATUSCODES.INTERNAL_ERROR.status,
        val message :String = "",
    ) : TaskCaseStates()
}

enum class STATUSCODES(val status : String){
    SUCCESS("-1"),
    INTERNAL_ERROR("-2"),
    DATA_WRITE_ERROR("-3"),
    DATA_FETCH_ERROR("-4"),
    DATA_DELETE_ERROR("-5")
}