package com.example.hiitintervaltimer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.hiitintervaltimer.ui.data.SqlLiteManager
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.hiitintervaltimer.ui.commons.InputWindow
import com.example.hiitintervaltimer.ui.commons.MultipleChoiceField
import com.example.hiitintervaltimer.ui.commons.TextField
import com.example.hiitintervaltimer.ui.commons.Confirmation
import com.example.hiitintervaltimer.ui.commons.MultiWindowForm
import com.example.hiitintervaltimer.ui.data.IntervalModel
import com.example.hiitintervaltimer.ui.data.WORKOUT_FUNCTION
import com.example.hiitintervaltimer.ui.data.WorkoutModel

@Composable
fun NewWorkout(navController: NavController, db: SqlLiteManager, id: Int, modifier: Modifier, ) {
    val workout = db.getWorkout(id)
    var workoutName by remember { mutableStateOf("New Workout") }
    var workoutDesc by remember { mutableStateOf("This is an empty workout") }
    var workoutFunction by remember { mutableStateOf(WORKOUT_FUNCTION.WORKOUT) }
    var window by remember { mutableIntStateOf(0) }

    val inputs = arrayListOf(
        InputWindow(
            "This field is used for users, like you, to distinguish one workout from another. You may also hear your voice assistance mention this name before your workout starts.",
            TextField(
                "Workout Name",
                { submitted -> workoutName = submitted; window += 1 },
                workoutName
            )
        ),
        InputWindow(
            "This field describes your workout so you know extactly what your doing",
            TextField(
                "Description",
                { submiited -> workoutDesc = submiited; window += 1 },
                workoutDesc
            )
        ),
        InputWindow("This field tells both you and the app what this workout function is. Your choices are warm up, cool down, and workout, this allows easy access and workout plan building, as you can select a warm up, workout and cool down to play in order",
            MultipleChoiceField(
                "Workout Function",
                { submitted -> workoutFunction = WORKOUT_FUNCTION.valueOf(submitted); window += 1 },
                WORKOUT_FUNCTION.values().map { it.name } // Maps enum to list of string values
            )),
        InputWindow("This window is asking you to confirm your choices. Review the values in the window and press confirm to continue",
            Confirmation(
                {
                val workoutId = db.addWorkout(WorkoutModel(id, workoutName, workoutDesc, workoutFunction, emptyList<IntervalModel>()))
                navController.navigate("workout/add/${workoutId}")
            },
            {
                navController.navigate("home")
            },
                { field: String ->
                    window = when (field) {
                        "Name" -> 0
                        "Description" -> 1
                        "Function" -> 2
                        else -> window
                    }
                },"",
                mapOf(
                    "Name" to workoutName,
                    "Description" to workoutDesc,
                    "Function" to workoutFunction.value
                )
            )))

    if (id > -1) {
        inputs.add(InputWindow("", UpdateInterval(navController, {submitted -> workout?.setIntervals(submitted)}, workout?.intervals ?: emptyList())))
    }

    Column {
        MultiWindowForm(navController, if(id > -1 ) "Update Workout" else "Create Workout", inputs, Modifier)
    }
}

@Composable
fun DraggableItem(interval: IntervalModel, onLongPress: () -> Unit, onDragMoved: (Int) -> Unit) {
    var isDragging by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.1f))
            .clickable {
                if (!isDragging) {
                    onLongPress() // Trigger long press action (navigate to com.example.hiitintervaltimer.ui.screen.AddInterval)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        val press = awaitPointerEventScope { awaitFirstDown() }
                        isDragging = true
                        // Handle drag movement here, pass position updates to onDragMoved
                        onDragMoved(0)  // You will need to implement actual drag movement logic
                    }
                )
            }
            .padding(16.dp)
    ) {
        Text(text = interval.name)
    }
}


@Composable
fun UpdateInterval(navController: NavController, onSubmit: (submit: List<IntervalModel>) -> Unit, intervalList: List<IntervalModel>
) {
    // Create a mutable state to hold the current list for reordering
    val reorderedIntervals by remember { mutableStateOf(intervalList) }

    // Handle drag and drop reordering and long press for updating
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        reorderedIntervals.map() { interval ->
            // Drag and drop item (you may use a custom drag-and-drop component or library)
            DraggableItem(
                interval = interval,
                onLongPress = {
                    // On long press, navigate to com.example.hiitintervaltimer.ui.screen.AddInterval to update this interval
                    navController.navigate("add_interval_screen/${interval.id}")
                }
            ) { newPosition ->
                // Update the position of the interval in the list
                val temp = interval.order
                interval.order = newPosition
                for (item in reorderedIntervals) {
                    if (item.order == newPosition) {
                        item.order = temp
                        break
                    }
                }
                reorderedIntervals.sortedBy {it.order}
            }
        }
    }

    // IconButton that triggers onSubmit when clicked
    IconButton(
        onClick = { onSubmit(reorderedIntervals) }
    ) {
        Icon(imageVector = Icons.Default.Check, contentDescription = "Submit Changes")
    }
}
