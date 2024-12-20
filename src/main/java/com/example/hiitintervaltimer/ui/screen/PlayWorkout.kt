import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.example.hiitintervaltimer.ui.data.CountedInterval
import com.example.hiitintervaltimer.ui.data.IntervalModel
import com.example.hiitintervaltimer.ui.data.SqlLiteManager
import com.example.hiitintervaltimer.ui.data.TimedInterval

@Composable
fun PlayWorkout(navController: NavController, db: SqlLiteManager, id : Int) {
    val workout = db.getWorkout(id)
}

@Composable
fun TimedRep(interval: TimedInterval) {
    var time = {mutableStateOf(interval.value)}

    // a screen that displays the count down
    // it should use android text to speech to warn the user as the timer is running out, at 1 minute, 30 seconds, and 10 seconds
    // it should tell user it is finished

}

@Composable
fun CountedRep(interval:CountedInterval) {
    var count = {mutableStateOf(interval.value)}
}