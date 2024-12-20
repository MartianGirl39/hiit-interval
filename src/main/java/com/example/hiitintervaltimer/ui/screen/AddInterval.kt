@Composable
fun addInterval(navController: NavController, db: SqlLiteManager, id: Int, order: Int, modifier: Modifier, ) {
    // grab the current interval by workout id and order
    var type by remember { mutableStateOf(INTERVAL_TYPE.TIMED) }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Add Intervals To ${workout.name}", color = Color.White)
            if (type == null) {
                MultipleChoiceField(
                    "Interval Type",
                    { submitted ->
                        type = INTERVAL_TYPE.valueOf(submitted)
                        valueIdentifier = if (submitted == "Timed") "Interval Timer" else "Rep Count"
                        window += 1
                    },
                    INTERVAL_TYPE.values().map { it.name }
                )   // for interval type
            } else {
                when(type) {
                    INTERVAL_TYPE.TIMED -> TimedIntervalForm({submitted -> db.appendInterval(submitted, id)}, {navController.navigate("home")})
                    INTERVAL_TYPE.COUNTED -> CountedIntervalForm({submitted -> db.appendInterbal(submitted, id)}, {navController.navigate("home")})
                }
            }
        }
    }
}

// need to make all values able to initilatzw
@Composable
TimedIntervalForm(onSubmit: (submitted: TimedInterval -> Unit, onCancel: () -> Unit) {
    var name by remember { mutableStateOf("New Interval") }
    var desc by remember { mutableStateOf("This is an empty interval") }
    var time by remember { mutableIntStateOf(0) }
    var delay by remember { mutableIntStateOf(0) }
    val order = -1 // this will be passed by parameter
    var window by remember { mutableIntStateOf(0) }
    val input = listOf(
        InputWindow("",
            TextField(
                "Interval Name",
                { submitted -> name = submitted; window += 1},
                name
            )),
        InputWindow("",
            TextField(
                "Interval Description",
                { submitted -> desc = submitted; window += 1},
                desc
            )),
        InputWindow("",
            ClockField(
                "Count Down",
                { submitted -> time = submitted; window += 1 },
                time
            )),
        InputWindow("Sometimes you can't immediately go on to the next thing. Sometime it takes time. This is your chance to take the matter into your own hands. Insert a delay after the interval to give you time to prepare for the next!",
            IntegerField(
                "Delay Before Next Interval",
                { submitted -> delay = submitted; window += 1 },
                delay
            )),
        InputWindow("",
            Confirmation(
                {onSubmit(TimedInterval(name, desc, time, delay, order))},
                {onCancel()},
                {field -> when(field) {
                    "Name" -> window = 0;
                    "Description" -> window = 1;
                    "Count Down" -> window = 2;
                    "Delay" -> window = 3;
                }},
                mapOf("Name" to name, "Description" to desc, "Count Down" to time, "Delay" to delay)
            ))
    )

    Column {
        val input = inputs[window]
        Form(input.help, input.value)
    }
}

@Composable
CountedIntervalForm() {
    var name by remember { mutableStateOf("New Interval") }
    var desc by remember { mutableStateOf("This is an empty interval") }
    var count by remember { mutableIntStateOf(0) }
    var delay by remember { mutableIntStateOf(0) }
    var repSpeed by remember { mutableIntState(0) }
    var order = -1
    var window by remember { mutableIntStateOf(0) }
    val input = listOf(
        InputWindow("",
            TextField(
                "Interval Name",
                { submitted -> name = submitted; window += 1},
                name
            )),
        InputWindow("",
            TextField(
                "Interval Description",
                { submitted -> desc = submitted; window += 1},
                desc
            )),
        InputWindow("",
            ClockField(
                "Rep Count",
                { submitted -> count = submitted; window += 1 },
                time
            )),
        InputWindow("Sometimes you can't immediately go on to the next thing. Sometime it takes time. This is your chance to take the matter into your own hands. Insert a delay after the interval to give you time to prepare for the next!",
            IntegerField(
                "Delay Before Next Interval",
                { submitted -> delay = submitted; window += 1 },
                delay
            )),
        InputWindow("",
            IntegerField(
                "Rep Duration",
                { submitted -> repSpeed = submitted; window += 1 },
                delay
            ))
        InputWindow("",
            Confirmation(
                {onSubmit(CountedInterval(name, desc, count, delay, repSpeed, order))},
                {onCancel()},
                {field -> when(field) {
                    "Name" -> window = 0;
                    "Description" -> window = 1;
                    "Rep Count" -> window = 2;
                    "Delay" -> window = 3;
                    "Rep Duration" -> window = 4
                }},
                mapOf("Name" to name, "Description" to desc, "Rep Count" to time, "Delay" to delay, "Rep Duration" to repSpeed)
            ))
    )

    Column {
        val input = inputs[window]
        Form(input.help, input.value)
    }
}