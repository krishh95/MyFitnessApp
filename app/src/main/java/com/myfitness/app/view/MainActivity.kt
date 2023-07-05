package com.myfitness.app.view

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.myfitness.app.R
import com.myfitness.app.data.dataBase.MyFitnessDb
import com.myfitness.app.data.dataBase.tables.Task
import com.myfitness.app.domain.TaskUseCases
import com.myfitness.app.ui.theme.MyFitnessTheme
import com.myfitness.app.view.utils.TaskCaseStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFitnessTheme {
                val isEditFiledOpen = remember{ mutableStateOf("") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.white)
                ) {

                    Scaffold(
                        modifier = Modifier.background(colorResource(id = R.color.white)),
                        floatingActionButton = {
                            FloatingActionButton(
                                modifier = Modifier.size(40.dp),
                                onClick = {
                                    isEditFiledOpen.value = if(isEditFiledOpen.value == "Not Opened"){
                                        "Opened"
                                    }else{
                                        "Not Opened"
                                    }
                                },
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Image(
                                    painter = painterResource(
                                        id = R.drawable.baseline_add_24
                                    ),
                                    contentDescription = ""
                                )
                            }
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        topBar = {
                            TopAppBar(
                                navigationIcon = {},
                                title = {
                                    Text(
                                        text="My Fitness Task App"
                                    )
                                }
                            )
                        },
                    ) { padding->

                        Body(
                            applicationContext,
                            padding,
                            isEditFiledOpen.value
                        ) { isEditFiledOpen.value = it }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class, InternalComposeApi::class)
@Composable
fun Body(
    context: Context?,
    padding: PaddingValues?,
    isEditFiledOpen: String,
    function: (String) -> Unit,
) {
    val editingPageId = remember {
        mutableStateOf("-1222")
    }
    val coroutineScope = rememberCoroutineScope()
    val db = remember {
        MyFitnessDb.getInstance(context!!)
    }

    val dao = remember {
        mutableStateOf(
            TaskUseCases(
                //taskDao = db.taskDao(),
                dbRef = Firebase.firestore.collection("Tasks")
            )
        )
    }

    val keys = remember{
       mutableStateListOf<String>()
    }

    LaunchedEffect(key1 = true, block ={
        val list = mutableListOf<String>()
        for(i in 1..3){
            list.add(
                UUID.randomUUID().toString().replace("-","").substring(0,15)
            )
        }
        keys.addAll(list)

    } )

    val pageState= rememberPagerState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding!!)
    ) {

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            count = keys.size,
            state = pageState,
            reverseLayout = true
        ) { currentPage ->
            TaskList(
                keys[currentPage],
                Modifier.fillMaxSize(),
                coroutineScope,
                dao.value,
                currentPage,
                isEditFiledOpen,
                editingPageId,
                function
            ){pageId : String->
                keys.removeAt(pageState.currentPage)
                keys.add(
                    pageState.currentPage,
                    UUID.randomUUID().toString().replace("-","").substring(0,15)
                )
            }
        }

    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun TaskList(
    pageId: String,
    modifier: Modifier,
    coroutineScope: CoroutineScope,
    db: TaskUseCases,
    currentPage: Int,
    isEditFiledOpen: String,
    isEditing: MutableState<String>,
    function: (String) -> Unit,
    update : (String) -> Unit
) {

    val tasks = remember {
        mutableStateListOf<Task>()
    }
    val page = remember {
        mutableStateOf(pageId)
    }

    val date = remember {
        Calendar.getInstance()
    }

    val isLoading = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        page.value,
        block = {
                isLoading.value = true
                date.timeInMillis = Date().time
                date.add(Calendar.DATE, -currentPage)

                tasks.clear()
                db.getAllTasks(date.timeInMillis) { taskCases ->
                    when (taskCases) {
                        is TaskCaseStates.Failed -> {
                            isLoading.value = false
                        }

                        is TaskCaseStates.Success<*> -> {
                            val listData = taskCases.data as? List<Task>
                            if (!listData.isNullOrEmpty()) {
                                tasks.addAll(listData)
                            }
                            isLoading.value = false
                        }
                    }
                }
        }
    )

    Box(
        modifier = modifier.background(
            colorResource(
                id = R.color.white
            )
        )
    ){
        LazyColumn(
            Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    colorResource(
                        id = R.color.off_white
                    )
                )
                .matchParentSize()){
            item{
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    text = SimpleDateFormat("dd MMM yyyy",Locale.US).format(date.time),
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                )
            }
            if(isLoading.value){
                item {
                    Box(modifier = modifier.fillParentMaxSize()){
                        CircularProgressIndicator(
                            modifier = Modifier
                                .then(
                                    Modifier
                                        .size(62.dp)
                                        .align(Alignment.Center)
                                )
                        )
                    }

                }
            }
            if(tasks.isEmpty()){
                item {
                    Box(modifier = modifier
                        .fillParentMaxSize()) {
                        Text(
                            text = "No Data Found",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
                return@LazyColumn
            }

            items(tasks.size){ index->
                Box(
                    Modifier
                ) {
                    Row {
                        Checkbox(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            checked = tasks[index].status,
                            enabled = !tasks[index].status,
                            onCheckedChange = {
                                val model = tasks[index]
                                tasks.remove(model)

                                val newModel = model.copy(status = !model.status)

                                tasks.add(index, newModel)

                            },
                            colors = CheckboxDefaults.colors(
                                disabledCheckedColor = MaterialTheme.colorScheme.primary,
                                checkedColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .fillMaxSize()
                                .fillMaxHeight(),
                            text = tasks[index].taskName,
                            textAlign = TextAlign.Start,
                            color = if (tasks[index].status) {
                                Color.Gray
                            } else {
                                Color.Black
                            }
                        )
                    }
                }

            }
        }

        if(isEditFiledOpen == "Not Opened") {
            ShowAddTaskDialog(coroutineScope,db){
                isEditing.value = UUID.randomUUID().toString().replace("-","").substring(0,15)

                function(it)
                update(page.value)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAddTaskDialog(
    coroutineScope: CoroutineScope,
    db: TaskUseCases,
    taskAdded: (String) -> Unit
) {
    val taskContent = remember{ mutableStateOf("") }
    val showSnackBar = remember{ mutableStateOf("") }

    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        onDismissRequest = {
            taskAdded("opened")
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AbsoluteCutCornerShape(5.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(20.dp),
                    text = "Add Task Here."
                )
                TextField(
                    value = taskContent.value,
                    onValueChange = {
                        taskContent.value = it
                    }
                )
                Button(
                    modifier = Modifier
                        .width(180.dp)
                        .height(60.dp)
                        .padding(10.dp),
                    onClick = {
                        val success = {
                            taskAdded("opened")
                        }
                        val failed = {reason:String ->
                            showSnackBar.value = reason
                        }
                        addTask(
                            coroutineScope,
                            db,
                            taskContent,
                            success,
                            failed
                        )
                    },
                ) {
                    Text(
                        text = "Save"
                    )
                }
            }

            if (showSnackBar.value.isNotBlank()) {
                Snackbar(
                    action = {
                        Button(
                            onClick = {
                                showSnackBar.value = ""
                            }
                        ) {
                            Text("dismiss")
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(text = showSnackBar.value)
                }

                val countDownTimer =
                    object : CountDownTimer(3000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {}

                        override fun onFinish() {
                            showSnackBar.value = ""
                        }
                    }

                DisposableEffect(key1 = showSnackBar.value) {

                    countDownTimer.start()

                    onDispose {
                        countDownTimer.cancel()
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MyFitnessTheme {
        val status = remember{
            mutableStateOf("opened")
        }
        Body(null, null, status.value) {  }
    }
}

fun addTask(
    coroutineScope: CoroutineScope,
    db: TaskUseCases,
    taskContent: MutableState<String>,
    success: () -> Unit,
    failed: (String) -> Unit
) {
    coroutineScope.launch(Dispatchers.IO) {
            val date = Calendar.getInstance()
            date.timeInMillis =Date().time

            db.saveTask(
                Task(
                    taskName = taskContent.value,
                    status = false,
                    id = UUID.randomUUID().toString().replace("-","").substring(0,7),
                    time = date.timeInMillis
                )
            ){
                when(it){
                    is TaskCaseStates.Failed -> {
                        failed(it.message)
                    }
                    is TaskCaseStates.Success<*> -> {
                        taskContent.value = ""
                        success()
                    }
                    
                }
                
            }
            
    }
}