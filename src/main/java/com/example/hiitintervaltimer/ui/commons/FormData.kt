package com.example.hiitintervaltimer.ui.commons

import androidx.compose.runtime.Composable

class InputWindow(val fieldName: String, val help: String, val view: @Composable (onSubmit: ()-> Unit) -> Unit){}
