package com.example.hiitintervaltimer.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hiitintervaltimer.ui.data.SqlLiteManager
import com.example.hiitintervaltimer.ui.data.WorkoutModel

@Composable
fun Home(navController: NavHostController, db: SqlLiteManager, modifier: Modifier = Modifier) {
    // Retrieve the list of workouts from the database (assuming this is a suspend function)
    val workouts = db.getAllWorkouts()

    // Column that holds all workout items
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        WorkoutList(workouts = workouts, navController = navController)
    }
}

@Composable
fun WorkoutList(workouts: List<WorkoutModel>, navController: NavHostController) {
    // If no workouts exist, show a placeholder message
    if (workouts.isEmpty()) {
        Text(
            text = "No workouts available. Please add one!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
        )
    } else {
        // For each workout, create a clickable card
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(workouts) { workout ->
                WorkoutListing(workout = workout, navController = navController)
            }
        }
    }
}

@Composable
fun WorkoutListing(workout: WorkoutModel, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Navigate to the workout details screen using the workout id
                navController.navigate("workout/${workout.id}")
            }
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(12.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = workout.function.value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = workout.desc,
                style = TextStyle(color = Color.Gray),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
