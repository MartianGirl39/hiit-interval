package com.example.hiitintervaltimer.ui.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

data class InputWindow(help: String, view: Composable)

@Composable
fun TextField(name: String, onNext: (submitted: String) -> Unit, workoutName: String) {
    Column {
        OutlinedTextField(
            value = workoutName,
            onValueChange = { workoutName = it },
            label = { Text(name) },
            placeholder = { Text("Enter $name Here: ") }
        )
        IconButton(
            onClick = { onNext(workoutName) },
        ) {
            Icon(Icons.Default.Check, contentDescription = "Next", tint = Color.White)
        }
    }
}

@Composable
fun MultipleChoiceField(name: String, onNext: (choice: String) -> Unit, fields: List<String>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        for (field in fields) {
            OutlinedButton(onClick = { onNext(field) }) {
                Text(field)
            }
        }
    }
}

@Composable
fun IntegerField(name: String, onNext: (submitted: Int) -> Unit, count: String) {
    var countState by remember { mutableStateOf(count.toIntOrNull() ?: 0) }  // Initialize count as an integer

    Column {
        OutlinedTextField(
            value = countState.toString(),
            onValueChange = {
                // Safely convert input to integer or leave it as 0
                countState = it.toIntOrNull() ?: 0
            },
            label = { Text(name) },
            placeholder = { Text("Enter $name Here:") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number // Enforce number input
            )
        )
        IconButton(
            onClick = { onNext(countState) }, // Pass the current integer value on click
        ) {
            Icon(Icons.Default.Check, contentDescription = "Next", tint = Color.White)
        }
    }
}


@Composable
fun ClockField(name: String, onNext: (submitted: Int) -> Unit, timeInSeconds: Int) {
    // Initialize minute and second values from the provided timeInSeconds
    var minuteHand by remember { mutableStateOf(timeInSeconds / 60) }
    var secondHand by remember { mutableStateOf(timeInSeconds % 60) }

    Column {
        // Input field for minutes
        OutlinedTextField(
            value = minuteHand.toString(),
            onValueChange = {
                // Ensure that the input is a valid number and update the minute hand
                minuteHand = it.toIntOrNull() ?: 0
            },
            label = { Text("Minutes") },
            placeholder = { Text("Enter minutes") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        // Input field for seconds
        OutlinedTextField(
            value = secondHand.toString(),
            onValueChange = {
                // Ensure that the input is a valid number and update the second hand
                secondHand = it.toIntOrNull() ?: 0
            },
            label = { Text("Seconds") },
            placeholder = { Text("Enter seconds") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )

        // Next Button
        IconButton(
            onClick = {
                // Combine minutes and seconds back into total seconds
                val totalTimeInSeconds = (minuteHand * 60) + secondHand
                onNext(totalTimeInSeconds) // Pass total time in seconds
            }
        ) {
            Icon(Icons.Default.Check, contentDescription = "Next", tint = Color.White)
        }
    }
}


@Composable
fun Form(help: String, input: @Composable () -> Unit) {
    val openAlertDialog = remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { openAlertDialog.value = true },
            ) {
                Icon(Icons.Default.Info, contentDescription = "Help", tint = Color.White)
            }
        }
        if (openAlertDialog.value) {
            HelpDialog(
                onConfirmation = { openAlertDialog.value = false },
                dialogText = help
            )
        }
        input()
    }
}

@Composable
fun HelpDialog(onConfirmation: () -> Unit, dialogTitle: String, dialogText: String) {
    AlertDialog(
        onDismissRequest = { onConfirmation() },
        icon = { Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.Black) },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text("Ok")
            }
        }
    )
}

@Composable
fun Confirmation(onConfirmation: () -> Unit, onCancel() -> Unit, onUpdate(field: String) -> Unit, dialog: String, fields: Map<String, String>) {
    Column {
        Text("Does this look right?")
        for (field in fields) {
            Row {
                Text("${field.key}: ${field.value}")
                OutlinedButton(onClick = { onUpdate(field.key) }) {
                    Text("Update ${field.key}")
                }
            }
        }
        OutlinedButton(onClick = { onCancel }) {
            Text("Cancel")
        }
        OutlinedButton(onClick = { onConfirmation }) {
            Text("Confirm")
        }
    }
}