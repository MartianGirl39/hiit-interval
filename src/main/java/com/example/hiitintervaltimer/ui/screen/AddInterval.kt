@Composable
fun addInterval(navController: NavController, db: SqlLiteManager, modifier: Modifier, id: Int) {
    val workout = db.getWorkout(id)

    // Local states
    var name by remember { mutableStateOf("New Interval") }
    var desc by remember { mutableStateOf("This is an empty interval") }
    var type by remember { mutableStateOf(INTERVAL_TYPE.TIMED) }
    var value by remember { mutableIntStateOf(0) }
    var valueIdentifier by remember { mutableStateOf("Time") }
    var delay by remember { mutableIntStateOf(0) }
    var window by remember { mutableIntStateOf(0) }

    // Input windows list
    val inputs = listOf(
        InputWindow(
            "The interval type alters how this interval behaves. There are two types, timed and counted. \n\nCounted works by counting down slowly with a delay between each count, this works well for interval where you measure in reps. For instance, a counted interval would be a great choice if you plan on doing 12 push ups or 50 jumping jacks for this interval. \n\nTimed works by initiating a count down from the a selected time in minutes until zero, this is good for when you measure in seconds or minutes. For instance, a timed interval would be a great choice if you plan on holding plank for 30 seconds or running for 15 minutes for this interval",
            MultipleChoiceField(
                "Interval Type",
                { submitted ->
                    type = INTERVAL_TYPE.valueOf(submitted)
                    valueIdentifier = if (submitted == "Timed") "Interval Timer" else "Rep Count"
                    window += 1
                },
                INTERVAL_TYPE.values().map { it.name }
            )
        ),
        InputWindow("",
            TextField(
                "Interval Name",
                { submitted -> name = submitted; window += 1 },
                name
            )),
        InputWindow("",
            TextField(
                "Interval Description",
                { submitted -> desc = submitted; window += 1 },
                desc
            )),
        // Dynamically show interval type fields
        InputWindow("",
            if (type == INTERVAL_TYPE.COUNTED) {
                IntegerField(
                    valueIdentifier,
                    { submitted -> value = submitted; window += 1 },
                    value
                )
            } else {
                ClockField(valueIdentifier, { submitted -> value = submitted; window += 1 }, value)
            }),
        InputWindow(
            "",
            IntegerField("Post Interval Delay", { submitted -> delay = submitted; window += 1 }, delay.toString())
        ),
        InputWindow(
            "",
            Confirmation(
                onConfirmation = {
                    // Logic for saving interval to database or navigation can go here
                    // Example: db.addInterval(name, desc, type, value, delay)
                    navController.navigate("workout/add/$id")
                },
                onCancel = { navController.navigate("home") },
                onUpdate = { field ->
                    // Handle field updates, e.g., based on window state
                },
                dialog = "Review the interval details before confirming.",
                fields = mapOf(
                    "Name" to name,
                    "Description" to desc,
                    "Type" to type.name,
                    "Value" to value.toString(),
                    "Delay" to delay.toString()
                )
            )
        )
    )

    // Display UI with the inputs list
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Add Intervals To ${workout.name}", color = Color.White)
            val input = inputs[window]
            Form(input.help, input.view)
        }
    }
}
