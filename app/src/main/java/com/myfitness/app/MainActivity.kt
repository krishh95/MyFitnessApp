package com.myfitness.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.myfitness.app.storage.MyFitnessDb
import com.myfitness.app.storage.tables.Task
import com.myfitness.app.ui.theme.MyFitnessTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFitnessTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isEditFiledOpen by remember{ mutableStateOf("") }

                    Scaffold(
                        floatingActionButton = {
                            FloatingActionButton(
                                modifier = Modifier.size(40.dp),
                                onClick = {
                                isEditFiledOpen = if(isEditFiledOpen == "Not Opened"){
                                    "Opened"
                                }else{
                                    "Not Opened"
                                }
                            }, shape = CircleShape,
                                containerColor = Color.Red,
                                contentColor = Color.White,
                            ) {
                                Image(painter = painterResource(id = R.drawable.baseline_add_24), contentDescription = "")
                            }

                        },
                        floatingActionButtonPosition = FabPosition.End) { padding->
                        Greeting(applicationContext,padding,isEditFiledOpen){
                            isEditFiledOpen = "opened"
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting(context: Context?, padding: PaddingValues?,isEditFiledOpen :String,dataAdded:()->Unit) {
    val coroutineScope = rememberCoroutineScope()
    val db = remember {
        MyFitnessDb.getInstance(context!!)
    }
    val selectedTask = remember {
        mutableStateListOf<Task>()
    }
    var taskContent = remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit, block = {
        coroutineScope.launch {
            withContext(Dispatchers.IO){
                selectedTask.clear()
                selectedTask.addAll(db.taskDao().getAllTasks().toList())
            }
        }
    })

    Box(Modifier.fillMaxSize(),
    contentAlignment =
    if(selectedTask.isEmpty())
        Alignment.Center
    else
        Alignment.TopStart
    ) {
        LazyColumn{
            if(selectedTask.isEmpty()){
                item {
                    Box {
                        Text(
                            text = "No Data Found",
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                return@LazyColumn
            }
            items(selectedTask.size){ index->
                Row {
                    Checkbox(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        checked = selectedTask.get(index).status ,
                        onCheckedChange = {
                            /*val list = selectedTask.mapIndexed { mapIndex,it->
                                it.copy(status = (index == mapIndex))
                            }*/
                            val model = selectedTask.get(index)
                            selectedTask.remove(model)

                            val newModel = model.copy(status = !model.status)

                            selectedTask.add(index,newModel)
                            /*selectedTask.clear()
                            selectedTask.addAll(list)*/
                        })
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .fillMaxSize()
                            .fillMaxHeight(),
                        text = selectedTask.get(index).taskName,
                        textAlign = TextAlign.Start,
                    )
                }

            }
        }
        if(isEditFiledOpen == "Not Opened") {
            Row {
                TextField(value = taskContent.value, onValueChange = {
                    taskContent.value = it
                })
                Button(onClick = {
                    coroutineScope.launch {
                        withContext(Dispatchers.IO){
                            db.taskDao().addTask(
                                Task(
                                    taskName = taskContent.value,
                                    status = false,
                                    id = UUID.randomUUID().toString().replace("-","").substring(0,7)
                                )
                            )
                            taskContent.value = ""
                            dataAdded()
                            selectedTask.clear()
                            selectedTask.addAll(db.taskDao().getAllTasks().toList())
                        }
                    }

                }) {
                    Text(text = "Save")
                }

            }

        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MyFitnessTheme {
        Greeting(null, null,"opened"){

        }
    }
}

fun getTaskList():ArrayList<Pair<String,Boolean>>{
    return ArrayList<Pair<String,Boolean>>().apply {
        add(Pair("a",true))
        add(Pair("b",false))
        add(Pair("c",false))
        add(Pair("d",false))
        add(Pair("e",false))
    }
}