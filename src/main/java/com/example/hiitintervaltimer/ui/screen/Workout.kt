import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.navigation.NavHostController
import com.example.hiitintervaltimer.ui.data.RepModel
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
                style = MaterialTheme.typography.h4,
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.h6
                )
            }
            if (descriptionVisible) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = workout.desc,
                    style = MaterialTheme.typography.body1,
                    color = Color.Gray
                )
            }

            // Button to start the workout
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navController.navigate("play-workout")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Start Workout")
            }

            // List of intervals (reps)
            Spacer(modifier = Modifier.height(16.dp))
            workout.intervals.forEach { interval ->
                Interval(interval)
            }
        } else {
            Text("Workout not found", style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
fun Interval(interval: RepModel) {
    // Layout for displaying the interval (rep) details
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.LightGray.copy(alpha = 0.2f), shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
    ) {
        Text(
            text = interval.name,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = interval.desc,
            style = MaterialTheme.typography.body1,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Value: ${interval.value}",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Details: ${interval.details}",
            style = MaterialTheme.typography.body2,
            color = Color.DarkGray
        )
    }
}
