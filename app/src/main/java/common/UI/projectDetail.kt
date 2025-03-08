package com.rezarashidi.common.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.rezarashidi.common.Projects
import com.rezarashidi.common.Tasks
import com.rezarashidi.common.TodoDatabaseQueries
import com.rezarashidi.metatask.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.time.LocalDate

@Composable
fun projectDetail(
    sheetState: MutableState<Boolean>,
    db: TodoDatabaseQueries,
    Project: MutableState<Projects?>? = null,
) {
    val projectName = remember { mutableStateOf(TextFieldValue(Project?.value?.Project_name ?: "")) }
    val descreption = remember { mutableStateOf(TextFieldValue(Project?.value?.Descreption ?: "")) }
    val dateStart = remember { mutableStateOf(Project?.value?.Startdate ?: "") }
    val dateEnd = remember { mutableStateOf(Project?.value?.Enddate ?: "") }
    val Urgency = listOf(stringResource(R.string.Low), stringResource(R.string.Medium), stringResource(R.string.High))
    var sliderPositionUrgency by remember { mutableStateOf(Project?.value?.Urgency?.toFloat() ?: 1F) }
    val pritoritynames = listOf(stringResource(R.string.Low), stringResource(R.string.Medium), stringResource(R.string.High))
    var sliderPosition by remember { mutableStateOf(Project?.value?.Pritority?.toFloat() ?: 1f) }
    val dialogStateStart = rememberMaterialDialogState()
    val dialogStateEnd = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()
//     val db= Projects()
    Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedTextField(projectName.value, onValueChange = {
            projectName.value = it
        }, label = {
            Text(stringResource(R.string.ProjectName))
        }, modifier = Modifier.fillMaxWidth().padding(10.dp))
        OutlinedTextField(descreption.value, onValueChange = {
            descreption.value = it
        }, label = {
            Text(stringResource(R.string.Description))
        }, modifier = Modifier.fillMaxWidth().padding(10.dp))
        Text(text = stringResource(R.string.Pritority)+": ", modifier = Modifier.align(alignment = Alignment.Start))
        Text(text = pritoritynames[sliderPosition.toInt() - 1])
        Slider(value = sliderPosition, onValueChange = { sliderPosition = it }, steps = 1, valueRange = 1f..3f)

        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            Text(text = stringResource(R.string.Urgency)+": ")
            Text(text = Urgency[sliderPositionUrgency.toInt() - 1])
        }


        Slider(
            value = sliderPositionUrgency,
            onValueChange = {
                sliderPositionUrgency = it

            },
            steps = 1,
            valueRange = 1f..3f
        )

        Text(text = stringResource(R.string.startDate), modifier = Modifier.align(alignment = Alignment.Start))

        MaterialDialog(dialogState = dialogStateStart, buttons = {
            positiveButton(stringResource(R.string.Ok))
            negativeButton(stringResource(R.string.Cancel))
        }) {
            datepicker(initialDate =if(dateStart.value!="")dateStart.value.toLocalDate().toJavaLocalDate()else LocalDate.now()) {
                dateStart.value = it.toString()
            }
        }
        OutlinedTextField(dateStart.value, onValueChange = {
        }, label = {
            Text( stringResource(R.string.startDate))
        }, readOnly = true, enabled = false, modifier = Modifier.fillMaxWidth().padding(10.dp).clickable {
            scope.launch {
                dialogStateStart.show()
            }
        })

        Text(text = stringResource(R.string.DueDate), modifier = Modifier.align(alignment = Alignment.Start))

        MaterialDialog(dialogState = dialogStateEnd, buttons = {
            positiveButton(stringResource(R.string.Ok))
            negativeButton(stringResource(R.string.Cancel))
        }) {
            datepicker (initialDate =if(dateEnd.value!="")dateEnd.value.toLocalDate().toJavaLocalDate()else LocalDate.now() ){
                dateEnd.value = it.toString()
            }
        }

        OutlinedTextField(dateEnd.value, onValueChange = {
        }, label = {
            Text( stringResource(R.string.Date))
        }, readOnly = true, enabled = false, modifier = Modifier.fillMaxWidth().padding(10.dp).clickable {
            scope.launch {
                dialogStateEnd.show()
            }
        })



        Row {
            OutlinedButton(
                {
                    scope.launch {
                    }

                    sheetState.value = false
                    Project?.value = null
                },
                border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(IntrinsicSize.Max).padding(10.dp)
            ) {
                Text( stringResource(R.string.close), style = MaterialTheme.typography.h5)
            }
            OutlinedButton(
                {
                    scope.launch {
                    }

                    db.insertProject(
                        Project?.value?.id,
                        projectName.value.text,
                        descreption.value.text,
                        sliderPosition.toLong(),
                        sliderPositionUrgency.toLong(),
                        if (dateStart.value == "") Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString() else dateStart.value,
                        if (dateEnd.value == "") null else dateEnd.value,

                        System.currentTimeMillis()
                    )

                    sheetState.value = false
                    Project?.value = null
                },
                border = BorderStroke(2.dp, color = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.width(IntrinsicSize.Max).padding(10.dp)
            ) {
                Text( stringResource(R.string.Save) ,style = MaterialTheme.typography.h5)


            }


        }
    }
}