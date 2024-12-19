package com.example.hiitintervaltimer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hiitintervaltimer.ui.data.IntervalModel
import com.example.hiitintervaltimer.ui.data.SqlLiteManager

@Composable
fun Workout(navController: NavHostController, db: SqlLiteManager, id: Int) {
    // Fetch workout details from the database by ID
    val workout = db.getWorkout(id)

    // Layout for the workout details
    Column(modifier = Modifier.padding(16.dp)) {
        if (workout != null) {
            // Display workout name
            Text(
                text = workout.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Toggleable workout description
            var descriptionVisible by remember { mutableStateOf(false) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (descriptionVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle description",
                    modifier = Modifier.clickable { descriptionVisible = !descriptionVisible }
                )
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            if (descriptionVisible) {
                Text(
                    text = workout.desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Button(
                onClick = {
                    navController.navigate("play-workout")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Start com.example.hiitintervaltimer.ui.screen.Workout")
            }
        } else {
            Text("workout not found", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

