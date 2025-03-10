package com.rezarashidi.common.UI

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.rezarashidi.common.Content
import com.rezarashidi.common.TodoDatabaseQueries
import com.rezarashidi.metatask.R

private enum class todotabs(val value: String) {
    PROJECTS("projects"),
    TASKS("tasks"),
    DAILIES("dailies")
}

@Composable
fun TabLayout(projects:@Composable () -> Unit,tasks:@Composable () -> Unit,dailies:@Composable () -> Unit,db: TodoDatabaseQueries) {
    val tabsName = listOf(stringResource(R.string.Projects),stringResource(R.string.Tasks),
            stringResource(R.string.dailies)
        )
    val selectedIndex = rememberSaveable() { mutableStateOf(1) }
    val icons = listOf(Icons.Default.Info, Icons.Default.Person, Icons.Default.ShoppingCart)

    Column {
        // Right now Tabs by default don't have changing like viewpager but I think we can handle
        // by overriding right/left swipe on content and updating state of selectedTab or using pager
        Surface(modifier = Modifier.weight(0.5f)) {
            when (selectedIndex.value) {
                todotabs.PROJECTS.ordinal -> {
                    projects()
                }
                todotabs.TASKS.ordinal -> {
                    tasks()
                }
                todotabs.DAILIES.ordinal -> {
                    dailies()
                }
            }
        }
        //Use ScrollableTabRow for list of tabs
        TabRow(
            selectedTabIndex = selectedIndex.value, backgroundColor = Color.White
          //  backgroundColor = MaterialTheme.colors.surface
        ) {
            tabsName.forEachIndexed { index, title ->
                Tab(
                    selected = index == selectedIndex.value,
                    onClick = {
                        when (index) {
                            todotabs.PROJECTS.ordinal -> {
                                selectedIndex.value = todotabs.PROJECTS.ordinal
                            }
                            todotabs.TASKS.ordinal -> {
                                selectedIndex.value = todotabs.TASKS.ordinal
                            }
                            todotabs.DAILIES.ordinal -> {
                                selectedIndex.value = todotabs.DAILIES.ordinal
                            }
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

    }
}