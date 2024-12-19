package com.example.hiitintervaltimer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.hiitintervaltimer.ui.commons.IntervalList
import com.example.hiitintervaltimer.ui.data.INTERVAL_OPTION
import com.example.hiitintervaltimer.ui.data.IntervalModel
import com.example.hiitintervaltimer.ui.data.WORKOUT_FUNCTION
import com.example.hiitintervaltimer.ui.data.WorkoutModel
import java.util.Locale

@Composable
fun NewWorkout(navController: NavController, db: SqlLiteManager, modifier: Modifier) {
    var workoutName by remember { mutableStateOf("New Workout") }
    var workoutDesc by remember { mutableStateOf("This is an empty workout") }
    var workoutFunction by remember { mutableStateOf(WORKOUT_FUNCTION.WORKOUT) }
    var expanded by remember { mutableStateOf(false) }
    var intervals by remember { mutableStateOf<List<IntervalModel>>(emptyList()) }

    // Dark theme background
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name:", color = Color.White)

            // Circular button with question mark for help
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    textStyle = TextStyle(color = Color.White)
                )
                IconButton(onClick = { /* Show popup explanation */ }) {
                    Icon(Icons.Default.Info, contentDescription = "Help", tint = Color.White)
                }
            }

            Text("Description:", color = Color.White)
            // Circular button with question mark for help
            BasicTextField(
                value = workoutDesc,
                onValueChange = { workoutDesc = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
                textStyle = TextStyle(color = Color.White)
            )

            Text("Function:", color = Color.White)

            // Dropdown menu for workout function
            Box {
                BasicTextField(
                    value = workoutFunction.value,
                    onValueChange = { workoutFunction = WORKOUT_FUNCTION.valueOf(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    textStyle = TextStyle(color = Color.White)
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(0.dp, 10.dp),
                    properties = PopupProperties(focusable = true)
                ) {
                    DropdownMenuItem(
                        onClick = {
                            workoutFunction = WORKOUT_FUNCTION.WORKOUT
                            expanded = false
                        },
                        text = { Text("Workout", color = Color.White) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            workoutFunction = WORKOUT_FUNCTION.WARM_UP
                            expanded = false
                        },
                        text = { Text("Warm Up", color = Color.White) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            workoutFunction = WORKOUT_FUNCTION.COOL_DOWN
                            expanded = false
                        },
                        text = { Text("Cool Down", color = Color.White) }
                    )
                }
            }

            // Interval form section
            IntervalForm(onAddInterval = { newInterval -> intervals = intervals + newInterval }, db = db)

            // Displaying the list of intervals added
            IntervalList(intervals = intervals)

            // Button to submit the workout
            Button(
                onClick = {
                    val workout = WorkoutModel(
                        -1,
                        name = workoutName,
                        desc = workoutDesc,
                        function = workoutFunction,
                        intervals = intervals
                    )
                    db.addWorkout(workout)
                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFF9B2D20)) // Brick Red
            ) {
                Text("Create Workout", color = Color.White)
            }
        }
    }
}

@Composable
fun IntervalForm(
    onAddInterval: (IntervalModel) -> Unit, db: SqlLiteManager) {
    var expanded by remember { mutableStateOf(false) }
    var IntervalName by remember { mutableStateOf("") }
    var IntervalDesc by remember { mutableStateOf("") }
    var IntervalType by remember { mutableStateOf(INTERVAL_OPTION.TIMED) }
    var IntervalValue by remember { mutableStateOf(0) }
    var IntervalDetails by remember { mutableStateOf("") }

    // Dark theme background for interval form
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Name field
            Text("Name:", color = Color.White)
            BasicTextField(
                value = IntervalName,
                onValueChange = { IntervalName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
                textStyle = TextStyle(color = Color.White)
            )

            // Description field
            Text("Description:", color = Color.White)
            BasicTextField(
                value = IntervalDesc,
                onValueChange = { IntervalDesc = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
                textStyle = TextStyle(color = Color.White)
            )

            // Interval Type dropdown menu
            Text("Type:", color = Color.White)
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
                    text = { Text("Timed", color = Color.White) }
                )
                DropdownMenuItem(
                    onClick = {
                        IntervalType = INTERVAL_OPTION.COUNTED
                        expanded = false
                    },
                    text = { Text("Counted", color = Color.White) }
                )
            }

            Text("Selected Option: ${IntervalType.toString().capitalize()}", color = Color.White)

            // Interval Value field
            Text("Value:", color = Color.White)
            BasicTextField(
                value = TextFieldValue(IntervalValue.toString()),
                onValueChange = { IntervalValue = it.text.toIntOrNull() ?: 0 },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
                textStyle = TextStyle(color = Color.White)
            )

            // Details field for counted interval type
            if (IntervalType == INTERVAL_OPTION.COUNTED) {
                Text("Details:", color = Color.White)
                BasicTextField(
                    value = IntervalDetails,
                    onValueChange = { IntervalDetails = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    textStyle = TextStyle(color = Color.White)
                )
            }

            Button(
                onClick = {
                    val interval = db.getProperType(IntervalType)
                    if (interval != null) {
                        onAddInterval(interval)
                    }
                    // Reset fields
                    IntervalName = ""
                    IntervalDesc = ""
                    IntervalValue = 0
                    IntervalDetails = ""
                },
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFF9B2D20)) // Brick Red
            ) {
                Text("Add Interval", color = Color.White)
            }
        }
    }
}
