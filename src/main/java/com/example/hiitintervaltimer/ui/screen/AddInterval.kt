package com.example.hiitintervaltimer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.hiitintervaltimer.ui.data.SqlLiteManager
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.hiitintervaltimer.ui.commons.ClockField
import com.example.hiitintervaltimer.ui.commons.InputWindow
import com.example.hiitintervaltimer.ui.commons.MultipleChoiceField
import com.example.hiitintervaltimer.ui.commons.TextField
import com.example.hiitintervaltimer.ui.commons.Confirmation
import com.example.hiitintervaltimer.ui.commons.Form
import com.example.hiitintervaltimer.ui.commons.IntegerField
import com.example.hiitintervaltimer.ui.data.CountedInterval
import com.example.hiitintervaltimer.ui.data.INTERVAL_OPTION
import com.example.hiitintervaltimer.ui.data.IntervalModel
import com.example.hiitintervaltimer.ui.data.TimedInterval


@Composable
fun AddInterval(navController: NavController, db: SqlLiteManager, id: Int, order: Int, modifier: Modifier, ) {
    val workout = db.getWorkout(id)
    val interval = db.getIntervalByWorkoutAndOrder(id, order)
    var type by remember { mutableStateOf(interval?.mapsToType()) }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (workout != null) {
                Text("Add Intervals To ${workout.name}", color = Color.White)
            }
            if (type == null) {
                MultipleChoiceField(
                    "Interval Type",
                    { submitted ->
                        type = INTERVAL_OPTION.valueOf(submitted)
                    },
                    INTERVAL_OPTION.entries.map { it.name }
                )   // for interval type
            } else {
                when(type) {
                    INTERVAL_OPTION.TIMED -> TimedIntervalForm({ submitted -> if(id > -1) db.updateIntervalInDb(submitted, id) else db.appendInterval(submitted, id)}, {navController.navigate("home")}, interval)
                    INTERVAL_OPTION.COUNTED -> CountedIntervalForm({submitted -> if(id > -1) db.updateIntervalInDb(submitted, id) else db.appendInterval(submitted, id)}, {navController.navigate("home")}, interval as CountedInterval?)
                    null -> navController.navigate("home")
                }
            }
        }
    }
}

@Composable
fun TimedIntervalForm(onSubmit: (interval: TimedInterval) -> Unit, onCancel: () -> Unit, interval: IntervalModel?) {
    val id = interval?.id ?: -1;
    var name by remember { mutableStateOf(interval?.name ?: "New Interval") }
    var desc by remember { mutableStateOf(interval?.desc ?: "This is an empty interval") }
    var time by remember { mutableIntStateOf(0) }
    var delay by remember { mutableIntStateOf(interval?.value ?: 0) }
    val order by remember { mutableIntStateOf(interval?.order ?: -1) }
    var window by remember { mutableIntStateOf(0) }
    val inputs = listOf(
        InputWindow("",
            TextField(
                "Interval Name",
                { submitted -> name = submitted; window += 1},
                name
            )),
        InputWindow("",
            TextField(
                "Interval Description",
                { submitted -> desc = submitted; window += 1},
                desc
            )),
        InputWindow("",
            ClockField(
                "Count Down",
                { submitted -> time = submitted; window += 1 },
                time
            )),
        InputWindow("Sometimes you can't immediately go on to the next thing. Sometime it takes time. This is your chance to take the matter into your own hands. Insert a delay after the interval to give you time to prepare for the next!",
            IntegerField(
                "Delay Before Next Interval",
                { submitted -> delay = submitted; window += 1 },
                delay.toString()
            )),
        InputWindow("",
            Confirmation(
                { onSubmit(TimedInterval(id, name, desc, time, delay, order)) },
                { onCancel() },
                { field ->
                    when (field) {
                        "Name" -> window = 0;
                        "Description" -> window = 1;
                        "Count Down" -> window = 2;
                        "Delay" -> window = 3;
                    }
                },
                "",
                mapOf(
                    "Name" to name,
                    "Description" to desc,
                    "Count Down" to time.toString(),
                    "Delay" to delay.toString()
                )
            ))
    )

    Column {
        val input = inputs[window]
        Form(input.help, { input.view })
    }
}

@Composable
fun CountedIntervalForm(onSubmit: (submitted: TimedInterval) -> Unit, onCancel: () -> Unit, interval: CountedInterval?) {
    val id = interval?.id ?: -1;
    var name by remember { mutableStateOf(interval?.name ?: "New Interval") }
    var desc by remember { mutableStateOf(interval?.desc ?: "This is an empty interval") }
    var count by remember { mutableIntStateOf(0) }
    var delay by remember { mutableIntStateOf(interval?.value ?: 0) }
    val order by remember { mutableIntStateOf(interval?.order ?: -1) }
    var repSpeed by remember { mutableIntStateOf(interval?.duration ?: 0) }
    var window by remember { mutableIntStateOf(0) }
    val inputs = listOf(
        InputWindow("",
            TextField(
                "Interval Name",
                { submitted -> name = submitted; window += 1},
                name
            )),
        InputWindow("",
            TextField(
                "Interval Description",
                { submitted -> desc = submitted; window += 1},
                desc
            )),
        InputWindow("",
            ClockField(
                "Rep Count",
                { submitted -> count = submitted; window += 1 },
                count
            )),
        InputWindow("Sometimes you can't immediately go on to the next thing. Sometime it takes time. This is your chance to take the matter into your own hands. Insert a delay after the interval to give you time to prepare for the next!",
            IntegerField(
                "Delay Before Next Interval",
                { submitted -> delay = submitted; window += 1 },
                delay.toString()
            )),
        InputWindow("",
            IntegerField(
                "Rep Duration",
                { submitted -> repSpeed = submitted; window += 1 },
                repSpeed.toString()
            )),
                InputWindow("",
        Confirmation(
            {CountedInterval(id, name, desc, count, delay, repSpeed, order)},
            {onCancel()},
            {field -> when(field) {
                "Name" -> window = 0;
                "Description" -> window = 1;
                "Rep Count" -> window = 2;
                "Delay" -> window = 3;
                "Rep Duration" -> window = 4
            }},"",
            mapOf("Name" to name.toString(), "Description" to desc.toString(), "Rep Count" to count.toString(), "Delay" to delay.toString(), "Rep Duration" to repSpeed.toString())
        ))
    )

    Column {
        val input = inputs[window]
        Form(input.help, { input.view })
    }
}