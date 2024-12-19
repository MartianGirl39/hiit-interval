package com.example.hiitintervaltimer.ui.data

data class WorkoutModel(val name: String, val desc: String, val function: String, val intervals: List<RepModel>)
data class RepModel(val mapsToTable : String, val name: String, val desc: String, val value: Int, val details: String)