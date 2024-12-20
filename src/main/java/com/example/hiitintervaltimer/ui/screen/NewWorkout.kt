package com.example.hiitintervaltimer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.hiitintervaltimer.ui.data.SqlLiteManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.example.hiitintervaltimer.ui.commons.IntervalList
import com.example.hiitintervaltimer.ui.data.INTERVAL_OPTION
import com.example.hiitintervaltimer.ui.data.IntervalModel
import com.example.hiitintervaltimer.ui.data.WORKOUT_FUNCTION
import com.example.hiitintervaltimer.ui.data.WorkoutModel

@Composable
fun NewWorkout(navController: NavController, db: SqlLiteManager, id: id, modifier: Modifier, ) {
    var workoutName by remember { mutableStateOf("New Workout") }
    var workoutDesc by remember { mutableStateOf("This is an empty workout") }
    var workoutFunction by remember { mutableStateOf(WORKOUT_FUNCTION.WORKOUT) }
    var window by remember { mutableIntStateOf(0) }

    MultiWindowForm(navController, id > -1 ? "Update Workout" : "Create Workout", listOf(
            InputWindow("This field is used for users, like you, to distinguish one workout from another. You may also hear your voice assistance mention this name before your workout starts.",
                TextField("Workout Name",
                    { submitted -> workoutName = submitted; window+=1},
                    workoutName
                )),
    InputWindow("This field describes your workout so you know extactly what your doing",
        TextField("Description",
            { submiited -> workoutDesc = submiited; window+=1 },
            workoutDesc))),
    InputWindow("This field tells both you and the app what this workout function is. Your choices are warm up, cool down, and workout, this allows easy access and workout plan building, as you can select a warm up, workout and cool down to play in order",
        MultipleChoiceField(
            "Workout Function",
            { submitted -> workoutFunction = WORKOUT_FUNCTION.valueOf(submitted); window += 1 },
            WORKOUT_FUNCTION.values().map { it.name } // Maps enum to list of string values
        ))
    InputWindow("This window is asking you to confirm your choices. Review the values in the window and press confirm to continue",
        Confirmation({
            val id = db.addWorkout(db)
            navController.navigate("workout/add/${id}")
        }
        {
            navController.navigate("home")
        }
        {
                field -> {
            if(field == "Name") window = 0
            else if(field == "Description") window = 1
            else if(field == "Function") window = 2
        }
            mapOf("Name" to workoutName, "Description" tp workoutDesc, "Function" to workoutFunction.value)
        })))
}