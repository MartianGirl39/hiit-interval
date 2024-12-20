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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hiitintervaltimer.ui.screen.Home
import com.example.hiitintervaltimer.ui.data.SqlLiteManager
import com.example.hiitintervaltimer.ui.screen.AddInterval
import com.example.hiitintervaltimer.ui.screen.NewWorkout
import com.example.hiitintervaltimer.ui.screen.PlayWorkout
import com.example.hiitintervaltimer.ui.screen.Workout

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
            Workout(navController = navController, db = SqlLiteManager(context = context), id = workoutId)
        }
        composable("workout/create") {
            NewWorkout(navController = navController, db = db, id =-1, modifier = Modifier)
        }
        composable(
            route = "workout/{id}/add/{order}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })){ backStackEntry ->
                val workoutId = backStackEntry.arguments?.getInt("id") ?: navController.navigate("/workout/create")
                val intervalOrder = backStackEntry.arguments?.getInt("order") ?: -1
                AddInterval(navController = navController, db=db, id=workoutId as Int, order=intervalOrder, Modifier)
            }
        composable(
            route = "worokout/{id}/update",
            arguments = listOf(navArgument("id") { type = NavType.IntType })){ backStackEntry ->
                val workoutId = backStackEntry.arguments?.getInt("id") ?: navController.navigate("/workout/create")
                NewWorkout(navController = navController, db =db, id = workoutId as Int, Modifier)
            }
        composable(
            route = "play/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("id") ?: -1
            PlayWorkout(navController = navController, db = SqlLiteManager(context), id = workoutId)
        }
    }
    Button(
        onClick = { navController.navigate("workout/create") },
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






