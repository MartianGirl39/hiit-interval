import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hiitintervaltimer.ui.data.SqlLiteManager

@Composable
fun AppNav(context: Context) {
    val navController = rememberNavController()
    val db = SqlLiteManager(context)

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(navController = navController, db = db)
        }
        composable(
            route = "workout/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("id") ?: -1
            Workout(navController = navController, db = SqlLiteManager(), id = workoutId)
        }
        composable("new-workout") {
            NewWorkout(navController = navController, db = db)
        }
        composable(
            route = "start-workout/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("id") ?: -1
            PlayWorkout(navController = navController, db = SqlLiteManager(), id = workoutId)
        }
    }
    Button(
        onClick = { navController.navigate("new-workout") },
        shape = CircleShape,
        modifier = Modifier
            .size(56.dp) // Adjust size of the button
            .padding(16.dp), // Adjust padding around the button
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary, // Button color
            contentColor = Color.White // Icon color
        ),
        elevation = ButtonDefaults.buttonElevation(8.dp), // Button elevation (shadow)
        contentPadding = PaddingValues(0.dp), // Remove default padding
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add new workout",
            tint = Color.White // Color of the icon
        )
    }
}




