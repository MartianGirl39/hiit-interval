import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.hiitintervaltimer.ui.data.SqlLiteManager
import com.example.hiitintervaltimer.ui.data.WorkoutModel

@Composable
fun Home(navController: NavHostController, db: SqlLiteManager, modifier: Modifier = Modifier) {
    val workouts = db.getAllWorkouts()
    Column {
        WorkoutList(workouts)
    }
}

@Composable
fun WorkoutList(workouts: List<WorkoutModel>) {
    for (workout in workouts) {
        WorkoutListing(workout)
    }
}

@Composable
fun WorkoutListing(workout: WorkoutModel) {
    Text(workout.name)
    Text(workout.function)
    Text(workout.desc)
}
