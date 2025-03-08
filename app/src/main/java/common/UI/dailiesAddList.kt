package com.rezarashidi.common.UI

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Projects
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries
import com.rezarashidi.metatask.R
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun dailiesAddList(sheetState: MutableState<Boolean>, db: TodoDatabaseQueries) {
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf(sortby.Default, sortby.Difficulty, sortby.Reward, sortby.Time, sortby.Urgency)
    val itemspair = mapOf(
        sortby.Default to stringResource(R.string.Default),
        sortby.Difficulty to stringResource(R.string.Difficulty),
        sortby.Reward to stringResource(
            R.string.Reward
        ),
        sortby.Time to stringResource(
            R.string.Time
        ),
        sortby.Urgency to stringResource(
            R.string.Urgency
        ),
    )
    val projects = db.getAllProjects().executeAsList().toMutableList().apply {
       add(0, Projects(0,"all","",0,0,"","",0))
    }

    var expanded by remember { mutableStateOf(false) }
    var expanded1 by remember { mutableStateOf(false) }
    var projectid by remember { mutableStateOf<Long>(0)}
    val openDialog= remember(){mutableStateOf(false)}
    val listoftask by remember (projectid,selectedIndex,openDialog.value){
        mutableStateOf(


        db.getAllTasks().executeAsList().asSequence().filter {
            it.Del == 0L
        }.filter {
            it.isdone == 0L
        }.filter {
            var x= db.getdailiessBytaskID(it.id).executeAsOneOrNull()
            if (
             x   == null

            ){
                true
            }else{

                x.del!=null
            }

        }
            .filter {
                if (projectid == 0L) {
                    true
                } else {
                    it.ProjectID == projectid
                }
            }.toList()
            .sortedByDescending {
                when (items[selectedIndex]) {
                    sortby.Default -> it.addTime
                    sortby.Urgency -> it.Urgency
                    sortby.Time -> ((it.timeInHour * 60) + it.timeInMinute)
                    sortby.Reward -> it.reward
                    sortby.Difficulty -> it.Difficulty
                    sortby.Isdone ->it.isdone
                }
            }.toMutableList()
        )
}



    Column( horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.selectProject))
            IconButton(onClick = { expanded1 = true }) {
                Icon(Icons.Filled.Sort, "backIcon")

                DropdownMenu(
                    expanded = expanded1,
                    onDismissRequest = { expanded1 = false },
//                modifier = Modifier.fillMaxWidth()
                )
                {
                    projects.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            expanded1 = false
                            projectid = s.id
                        }) {
                            Text(text = s.Project_name)
                        }
                    }
                }
            }




            Text(stringResource(R.string.sortby))
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.Sort, "backIcon")
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
//                modifier = Modifier.fillMaxWidth()
                )
                {
                    items.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            selectedIndex = index
                            expanded = false
                        }) {
                            itemspair[s]?.let { Text(text = it) }
                        }
                    }
                }
            }

        }

        if (listoftask.isEmpty()){

            Text(stringResource(R.string.Therenotask))
        }else{
            showlist(db,listoftask,openDialog,sheetState)
        }




        OutlinedButton(
            {
                sheetState.value = false
            }, border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Text(stringResource(R.string.close), style = MaterialTheme.typography.h5)
        }
    }
}
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun showlist(db: TodoDatabaseQueries,listoftask: MutableList<Tasks>,openDialog: MutableState<Boolean>,sheetState: MutableState<Boolean>){
    listoftask.forEach {
key(it.addTime){

    taskItem(db,it, mutableStateOf(false),openDialog,mutableStateOf(null),mutableStateOf(false),true,listoftask,sheetState)

}



//        if (openDialog.value){
//         listoftask.remove(it)
//            openDialog.value=!openDialog.value
//        }

    }
}