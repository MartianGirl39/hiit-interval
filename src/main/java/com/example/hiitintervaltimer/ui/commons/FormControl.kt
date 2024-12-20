package com.example.hiitintervaltimer.ui.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class InputWindow(val help: String, val view: Unit)

@Composable
fun TextField(fieldName: String, onNext: (submitted: String) -> Unit, workoutName: String) {
    var name by remember { mutableStateOf(workoutName) }
    Column {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(fieldName) },
            placeholder = { Text("Enter $fieldName Here: ") }
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
    var countState by remember { mutableIntStateOf(count.toIntOrNull() ?: 0) }  // Initialize count as an integer

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
    var minuteHand by remember { mutableIntStateOf(timeInSeconds / 60) }
    var secondHand by remember { mutableIntStateOf(timeInSeconds % 60) }

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
fun HelpDialog(onConfirmation: () -> Unit, dialogText: String) {
    AlertDialog(
        onDismissRequest = { onConfirmation() },
        icon = { Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.Black) },
        text = { Text(text = dialogText) },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text("Ok")
            }
        }
    )
}

@Composable
fun Confirmation(onConfirmation: () -> Unit, onCancel: () -> Unit, onUpdate: (field: String) -> Unit, dialog: String, fields: Map<String, String>) {
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
        OutlinedButton(onClick =onCancel) {
            Text("Cancel")
        }
        OutlinedButton(onClick =onConfirmation) {
            Text("Confirm")
        }
    }
}

@Composable
fun MultiWindowForm(navController: NavController, name: String, inputs: List<InputWindow>, modifier: Modifier, ) {
    var window by remember { mutableIntStateOf(0) }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(name, color = Color.White)
            val input = inputs[window]
            Form(input.help, { input.view })
        }
    }
}