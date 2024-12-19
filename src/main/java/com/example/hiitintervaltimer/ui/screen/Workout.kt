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
    val workout = db.getWorkout(id)

    Column {
        if (workout != null) {
            Text(workout.name)
            // text that can be made visible or invisable, default invisable with a drop down arrow
            Text(workout.desc)
            // play button that wstarts the workout
//            Button() { }
            for (interval in workout.intervals) {
                Interval(interval)
            }
        }
    }

}

@Composable
fun Interval(interval: RepModel) {
    Text(interval.name)
    
}
