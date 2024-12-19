package com.example.hiitintervaltimer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.hiitintervaltimer.ui.data.SqlLiteManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.hiitintervaltimer.ui.data.CountedInterval
import com.example.hiitintervaltimer.ui.data.INTERVAL_OPTION
import com.example.hiitintervaltimer.ui.data.IntervalModel
import com.example.hiitintervaltimer.ui.data.TimedInterval
import com.example.hiitintervaltimer.ui.data.WORKOUT_FUNCTION
import com.example.hiitintervaltimer.ui.data.WorkoutModel
import java.util.Locale

@Composable
fun NewWorkout(navController: NavController, db: SqlLiteManager, modifier: Modifier) {
    var workoutName by remember { mutableStateOf("New Workout") }
    var workoutDesc by remember { mutableStateOf("This is an empty workout") }
    var workoutFunction by remember { mutableStateOf(WORKOUT_FUNCTION.WORKOUT) }

    var intervals by remember { mutableStateOf<List<IntervalModel>>(emptyList()) }

    Column(modifier = Modifier.padding(16.dp)) {
        // Workout name input
        Text("Workout Name:")
        BasicTextField(
            value = workoutName,
            onValueChange = { workoutName = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Workout description input
        Text("Workout Description:")
        BasicTextField(
            value = workoutDesc,
            onValueChange = { workoutDesc = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Workout function input
        Text("Workout Function:")
        BasicTextField(
            value = workoutFunction.value,
            onValueChange = { workoutFunction = WORKOUT_FUNCTION.valueOf(it) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Interval Form: to add intervals
        IntervalForm( onAddInterval = { newInterval -> {intervals = intervals + newInterval}}, db=db )

        // Displaying the list of intervals added
        IntervalList(intervals = intervals)

        // Button to submit the workout
        Button(
            onClick = {
                val workout = WorkoutModel(
                    name = workoutName,
                    desc = workoutDesc,
                    function = workoutFunction,
                    intervals = intervals
                )
                db.addWorkout(workout)
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Create Workout")
        }
    }
}


@Composable
fun IntervalForm(
    onAddInterval: (IntervalModel) -> Unit, db: SqlLiteManager) {
    var expanded by remember { mutableStateOf(false) }
    var IntervalName by remember { mutableStateOf("") }
    var IntervalDesc by remember { mutableStateOf("") }
    var IntervalType by remember { mutableStateOf(INTERVAL_OPTION.TIMED) }   // timed or counted so far
    var IntervalValue by remember { mutableStateOf(0) }
    var IntervalDetails by remember { mutableStateOf("")
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Interval Name
        Text("Interval Interval Name:")
        BasicTextField(
            value = IntervalName,
            onValueChange = { IntervalName = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Interval Description
        Text("Interval Interval Description:")
        BasicTextField(
            value = IntervalDesc,
            onValueChange = { IntervalDesc = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Text("Interval Type")
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 10.dp),
            properties = PopupProperties(focusable = true)
        ) {
            DropdownMenuItem(
                onClick = {
                    IntervalType = INTERVAL_OPTION.TIMED
                    expanded = false
                },
                text = { Text("Timed") }
            )
            DropdownMenuItem(
                onClick = {
                    IntervalType = INTERVAL_OPTION.COUNTED
                    expanded = false
                },
                text = { Text("Counted") }
            )
        }
        Text("Selected Option: ${IntervalType.toString().toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)}")
        // Interval Value
        Text("Interval Value:")
        BasicTextField(
            value = TextFieldValue(IntervalValue.toString()),
            onValueChange = { IntervalValue = it.text.toIntOrNull() ?: 0 },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        if (IntervalType.value == "counted_interval") {
            // Interval Details
            Text("Interval Details:")
            BasicTextField(
                value = IntervalDetails,
                onValueChange = { IntervalDetails = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }

        Button(
            onClick = {
                val interval = db.getProperType(IntervalType)
                if (interval != null) {
                    onAddInterval(interval)
                }
                IntervalName = ""
                IntervalDesc = ""
                IntervalValue = 0
                IntervalDetails = ""
            },
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text("Add Interval")
        }
    }
}

@Composable
fun IntervalList(intervals: List<IntervalModel>) {
    intervals.forEachIndexed { index, interval ->
        when (interval) {
            is TimedInterval -> IntervalCard(interval)
            is CountedInterval -> IntervalCard(interval)
        }
    }
}

@Composable
fun IntervalCard(interval: TimedInterval) {
    Card(modifier = Modifier.padding(4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = interval.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = interval.desc,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Value: ${interval.value / 60}:${interval.value % 60} minutes",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Delay: ${interval.delay} seconds",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun IntervalCard(interval: CountedInterval) {
    Card(modifier = Modifier.padding(4.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = interval.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = interval.desc,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Value: ${interval.value}x",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Delay: ${interval.delay} seconds",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


