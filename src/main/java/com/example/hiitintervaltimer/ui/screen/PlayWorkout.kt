@Composable
fun PlayWorkout(navController = navController, db = SqlLiteManager(), id = workoutId) {
    val workout = db.getWorkout(workoutId)
}

@Composable
fun TimedRep(interval:RepoModel) {
    var time = {mutableStateOf(interval.value)}

    // a screen that displays the count down
    // it should use android text to speech to warn the user as the timer is running out, at 1 minute, 30 seconds, and 10 seconds
    // it should tell user it is finished

}

@Composable
fun CountedRep(interval:RepoModel) {
    var count = {mutableStateOf(interval.value)}
}