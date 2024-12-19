import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.hiitintervaltimer.ui.data.SqlLiteManager

@Composable
fun NewWorkout(navController: NavController, db: SqlLiteManager) {
    @Composable
    fun NewWorkout(navController: NavController, db: SqlLiteManager) {
        // State to hold the form inputs
        var workoutName by remember { mutableStateOf("") }
        var workoutDesc by remember { mutableStateOf("") }
        var workoutFunction by remember { mutableStateOf("") }
        var repName by remember { mutableStateOf("") }
        var repDesc by remember { mutableStateOf("") }
        var repValue by remember { mutableStateOf("") }
        var repDetails by remember { mutableStateOf("") }

        val repList = remember { mutableStateListOf<RepModel>() }
        val scope = rememberCoroutineScope()

        // Function to handle form submission
        fun saveWorkout() {
            if (workoutName.isEmpty() || workoutDesc.isEmpty() || workoutFunction.isEmpty() || repList.isEmpty()) {
                // Show error message
                Toast.makeText(navController.context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val workout = WorkoutModel(
                    name = workoutName,
                    desc = workoutDesc,
                    function = workoutFunction,
                    intervals = repList
                )
                // Assuming SqlLiteManager has a method to insert workout into the database
                scope.launch {
                    db.insertWorkout(workout)
                }
                Toast.makeText(navController.context, "Workout Saved", Toast.LENGTH_SHORT).show()
                navController.popBackStack()  // Navigate back after saving
            }
        }

        // Form UI for the new workout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Workout Details Input
            TextField(value = workoutName, onValueChange = { workoutName = it }, label = { Text("Workout Name") })
            TextField(value = workoutDesc, onValueChange = { workoutDesc = it }, label = { Text("Workout Description") })
            TextField(value = workoutFunction, onValueChange = { workoutFunction = it }, label = { Text("Workout Function") })

            Spacer(modifier = Modifier.height(16.dp))

            // Rep Model Inputs
            TextField(value = repName, onValueChange = { repName = it }, label = { Text("Rep Name") })
            TextField(value = repDesc, onValueChange = { repDesc = it }, label = { Text("Rep Description") })
            TextField(
                value = repValue,
                onValueChange = { repValue = it },
                label = { Text("Rep Value") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )
            TextField(value = repDetails, onValueChange = { repDetails = it }, label = { Text("Rep Details") })

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Validate and add the rep to the list
                if (repName.isNotEmpty() && repDesc.isNotEmpty() && repValue.isNotEmpty() && repDetails.isNotEmpty()) {
                    repList.add(
                        RepModel(
                            mapsToTable = "example_table",  // This is just an example, modify based on your actual table
                            name = repName,
                            desc = repDesc,
                            value = repValue.toInt(),
                            details = repDetails
                        )
                    )
                    // Clear the rep input fields
                    repName = ""
                    repDesc = ""
                    repValue = ""
                    repDetails = ""
                } else {
                    Toast.makeText(navController.context, "Please fill all rep fields", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Add Interval")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display added intervals
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(repList) { rep ->
                    Text("Rep: ${rep.name}, Value: ${rep.value}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Workout Button
            Button(onClick = { saveWorkout() }) {
                Text("Save Workout")
            }
        }
    }
}