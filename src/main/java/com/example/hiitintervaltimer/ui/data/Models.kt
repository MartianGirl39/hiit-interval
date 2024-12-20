package com.example.hiitintervaltimer.ui.data

import android.database.Cursor

enum class INTERVAL_OPTION(val value: String) {
    TIMED("timed_interval"),
    COUNTED("counted_interval")
}

enum class WORKOUT_FUNCTION(val value: String) {
    WARM_UP("warm up"),
    WORKOUT("workout"),
    COOL_DOWN("cool_down")
}

class WorkoutModel(var id: Int, var name: String, var desc: String, var function: WORKOUT_FUNCTION, var intervals: List<IntervalModel>){
    fun setInterval(intervals: List<IntervalModel>) {
        this.intervals = intervals
    }

    fun mapRowToSelf(cursor: Cursor): WorkoutModel {
        this.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        this.desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
        this.function = WORKOUT_FUNCTION.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("plan_function")))
        return this;
    }

    fun setIntervals(submitted: List<IntervalModel>) {
    this.intervals = submitted
    }
}

abstract class IntervalModel(var id: Int, var name: String, var desc: String, var value: Int, var delay: Int, var order: Int) {
    abstract fun mapsTo(function: () -> String):String
    abstract fun mapRowToUInterval(cursor: Cursor): IntervalModel
    abstract fun mapsToType(): INTERVAL_OPTION
}

class TimedInterval(id: Int, name: String, desc: String, value: Int, delay: Int, order: Int) : IntervalModel(id, name, desc, value, delay, order) {
    private final val mapsToTable: INTERVAL_OPTION = INTERVAL_OPTION.TIMED

    override fun mapsTo(function: () -> String): String {
        return mapsToTable.value
    }

    override fun mapRowToUInterval(cursor: Cursor): TimedInterval {
        this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        this.desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
        this.value = cursor.getInt(cursor.getColumnIndexOrThrow("value"))
        this.delay = cursor.getInt(cursor.getColumnIndexOrThrow("delay"))
        this.order = cursor.getInt(cursor.getColumnIndexOrThrow("order"))

        return this
    }

    override fun mapsToType(): INTERVAL_OPTION {
        return INTERVAL_OPTION.TIMED
    }
}

class CountedInterval(id: Int, name: String, desc: String, value: Int, delay: Int, order: Int, var duration: Int) : IntervalModel(id, name, desc, value, delay, order) {
    private final val mapsToTable: INTERVAL_OPTION = INTERVAL_OPTION.COUNTED
    override fun mapsTo(function: () -> String): String {
        return mapsToTable.value
    }

    override fun mapRowToUInterval(cursor: Cursor): CountedInterval {
        this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        this.desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
        this.value = cursor.getInt(cursor.getColumnIndexOrThrow("value"))
        this.delay = cursor.getInt(cursor.getColumnIndexOrThrow("delay"))
        this.order = cursor.getInt(cursor.getColumnIndexOrThrow("order"))
        this.duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
        return this
    }

    override fun mapsToType(): INTERVAL_OPTION {
        return INTERVAL_OPTION.COUNTED
    }
}



