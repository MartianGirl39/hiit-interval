package com.example.hiitintervaltimer.ui.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import kotlin.reflect.KProperty1

class SqlLiteManager(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "hiit_interval_database.db"
        private const val DATABASE_VERSION = 1
        private const val WORKOUT_STORE = "workout"
        private val WORKOUT_PROPS = listOf(
            "name VARCHAR(50)",
            "description TEXT",
            "plan_function VARCHAR(50)"
        )
        private val INTERVAL_TYPES = mapOf(
            "timed_interval" to listOf(
                "name VARCHAR(50)", // indentifier of the interval
                "description VARCHAR(300)", // dseciption said right before starting interval
                "time INTEGER", // time in seconds that the inteval will go on for
                "delay INTEGER", // delay between starting the next interval,
                "order INTEGER",
            ),
            "counted_interval" to listOf(
                "name VARCHAR(50)",
                "description VARCHAR(300)",
                "count INTEGER",
                "delay INTEGER",
                "order INTEGER",
                "duration INTEGER"
            )
        )
        private val INTERVAL_MODEL_MAPPING = mapOf(
            "timed_interval" to TimedInterval(-1 , "", "", 0, 0, 0),
            "counted_interval" to CountedInterval(-1, "", "", 0, 0, 0, 0)
        )
//        private val PRELOADED_WORKOUTS
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create the workout table
        val createWorkoutTable = """
            CREATE TABLE $WORKOUT_STORE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                used INTEGER DEFAULT 0,
                ${WORKOUT_PROPS.joinToString(",\n")}
            );
        """.trimIndent()

        db?.execSQL(createWorkoutTable)

        // Create interval tables
        for ((interval, props) in INTERVAL_TYPES) {
            val createIntervalTable = """
                CREATE TABLE $interval (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ${props.joinToString(",\n")},
                    workout INTEGER REFERENCES $WORKOUT_STORE(id)
                );
            """.trimIndent()
            db?.execSQL(createIntervalTable)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop all tables
        db?.execSQL("DROP TABLE IF EXISTS $WORKOUT_STORE")
        for (interval in INTERVAL_TYPES.keys) {
            db?.execSQL("DROP TABLE IF EXISTS $interval")
        }
        onCreate(db)
    }

    fun getProperType(type: INTERVAL_OPTION): IntervalModel? {
        return INTERVAL_MODEL_MAPPING[type.value]
    }

    // Refactored to remove db parameter
    fun addWorkout(workout: WorkoutModel) {
        val workoutId = insertWorkout(workout)
        workout.intervals.forEach { Interval ->
            insertInterval(Interval, workoutId)
        }
    }

    // Refactored to remove db parameter
    private fun insertWorkout(workout: WorkoutModel): Long {
        val db = writableDatabase // Get the writable database
        val insertWorkout = """
            INSERT INTO $WORKOUT_STORE (${WORKOUT_PROPS.joinToString(", ")})
            VALUES (${WORKOUT_PROPS.map { "?" }.joinToString(", ")})
        """
        val statement = db.compileStatement(insertWorkout)
        bindWorkoutModelValuesToStatement(statement, workout)
        return statement.executeInsert()
    }

    private fun bindWorkoutModelValuesToStatement(statement: SQLiteStatement, model: WorkoutModel) {
        var count = 0
        model::class.members.filterIsInstance<KProperty1<WorkoutModel, *>>()
            .forEach { property ->
                val value = property.get(model)
                if (property.name == "mapsTo" || property.name == "id" || value is Function<*>) {
                    when (value) {
                        is String -> statement.bindString(count, value)
                        is Long -> statement.bindLong(count, value)
                        is Double -> statement.bindDouble(count, value)
                        else -> statement.bindString(
                            count,
                            value.toString()
                        ) // Default to string if unknown type
                    }
                    count += 1
                }
            }
    }

    // Refactored to remove db parameter
    private fun insertInterval(Interval: IntervalModel, workoutId: Long) {
        val db = writableDatabase // Get the writable database
        val columns = INTERVAL_TYPES[Interval.mapsTo { "?," }]?.joinToString(", ") ?: ""
        val insertInterval = """
            INSERT INTO ${Interval.mapsTo { "?," }} ($columns, workout)
            VALUES (${INTERVAL_TYPES.get(Interval.mapsTo(){"?,"})} ?);
        """
        val IntervalStatement = db.compileStatement(insertInterval)
        bindIntervalModelValuesToStatement(IntervalStatement, Interval)
        IntervalStatement.bindLong(INTERVAL_TYPES.get(Interval.mapsTo { "?," })?.size?.plus(1) ?: 0, workoutId)
        IntervalStatement.executeInsert()
    }

    private fun bindIntervalModelValuesToStatement(statement: SQLiteStatement, model: IntervalModel) {
        var count = 0
        model::class.members.filterIsInstance<KProperty1<IntervalModel, *>>()
            .forEach { property ->
                val value = property.get(model)
                if (property.name == "mapsTo" || property.name == "id" || value is Function<*>) {
                    when (value) {
                        is String -> statement.bindString(count, value)
                        is Long -> statement.bindLong(count, value)
                        is Double -> statement.bindDouble(count, value)
                        else -> statement.bindString(
                            count,
                            value.toString()
                        ) // Default to string if unknown type
                    }
                    count += 1
                }
            }
    }

    // Refactored to remove db parameter
    fun updateWorkout(workoutId: Long, workout: WorkoutModel) {
        updateWorkoutInDb(workoutId, workout)
        workout.intervals.forEach { Interval ->
            updateIntervalInDb(Interval, workoutId)
        }
    }

    // Refactored to remove db parameter
    private fun updateWorkoutInDb(workoutId: Long, workout: WorkoutModel) {
        val db = writableDatabase // Get the writable database
        val updateWorkout = """
            UPDATE $WORKOUT_STORE
            SET ${WORKOUT_PROPS.joinToString(", ") { "$it = ?" }}
            WHERE id = ?;
        """
        val statement = db.compileStatement(updateWorkout)
        bindWorkoutModelValuesToStatement(statement, workout)
        statement.bindLong(workout::class.members.size + 1, workoutId)
        statement.executeUpdateDelete()
    }

    // Refactored to remove db parameter
    fun updateIntervalInDb(Interval: IntervalModel, workoutId: Long) {
        val db = writableDatabase // Get the writable database
        val updateInterval = """
            UPDATE ${Interval.mapsTo { "?," }}
            SET ${INTERVAL_TYPES[Interval.mapsTo { "?," }]?.joinToString(", ") { "$it = ?" }}
            WHERE workout = ?;
        """
        val IntervalStatement = db.compileStatement(updateInterval)
        bindIntervalModelValuesToStatement(IntervalStatement, Interval)
        val intervalSize = INTERVAL_TYPES[Interval.mapsTo { "?," }]?.size ?: 0
        IntervalStatement.bindLong(intervalSize + 1, workoutId)
        IntervalStatement.executeUpdateDelete()
    }

    // Refactored to remove db parameter
    fun getWorkout(id: Int): WorkoutModel? {
        val db = readableDatabase // Get the readable database
        val query = "SELECT * FROM $WORKOUT_STORE WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        var workout: WorkoutModel? = null

        if (cursor.moveToFirst()) {
            workout = mapRowToWorkout(cursor)
        }
        cursor.close()
        return workout
    }

    fun appendInterval(interval: IntervalModel, id: Int) {
        val db = readableDatabase
        val workout = getWorkout(id)
        val order = (workout?.intervals?.size ?: -2) + 1
        val insertInterval = """
            INSERT INTO ${interval.mapsTo { "?," }} (workout, ${INTERVAL_TYPES[interval.mapsTo { "?," }]?.joinToString(", ") { "$it = ?" }}) VALUES 
            ($id, ${INTERVAL_TYPES.map{"?,"}} ?);
        """.trimIndent()
        val statement = db.compileStatement(insertInterval)
        bindIntervalModelValuesToStatement(statement, interval)
        statement.bindLong(5, order.toLong())
        statement.executeInsert()
    }

    // Refactored to remove db parameter
    fun getAllWorkouts(): List<WorkoutModel> {
        val db = readableDatabase // Get the readable database
        val query = "SELECT * FROM $WORKOUT_STORE"
        val cursor = db.rawQuery(query, null)
        val workouts = mutableListOf<WorkoutModel>()

        while (cursor.moveToNext()) {
            val workout = mapRowToWorkout(cursor)
            workouts.add(workout)
        }
        cursor.close()
        return workouts
    }

    fun getIntervalByWorkoutAndOrder(id: Int, order: Int) : IntervalModel? {
        val db = readableDatabase
        //check every interval in list and when one is found return an interval of the appropriate type
        for (interval in INTERVAL_TYPES.keys) {
            val sql = "SELECT * FROM $interval WHERE id = ? AND order = ?"
            val cursor = db.rawQuery(sql, arrayOf(id.toString(), order.toString()))
            if (cursor.moveToFirst()) {
                val myInterval = INTERVAL_MODEL_MAPPING[interval]
                myInterval?.mapRowToUInterval(cursor)
                return myInterval
            }
        }
        return null
    }

    private fun getIntervalsForWorkout(workoutId: Int): List<IntervalModel> {
        val db = readableDatabase // Get the readable database
        val Intervals = mutableListOf<IntervalModel>()
        for (interval in INTERVAL_TYPES.keys) {
            val query = "SELECT * FROM $interval WHERE workout = ?"
            val cursor = db.rawQuery(query, arrayOf(workoutId.toString()))
            while (cursor.moveToNext()) {
                val myInterval = INTERVAL_MODEL_MAPPING[interval]
                myInterval?.mapRowToUInterval(cursor)?.let { Intervals.add(it) }
            }
            cursor.close()
        }
        return Intervals
    }

    private fun mapRowToWorkout(cursor: Cursor?): WorkoutModel {
        val workout = WorkoutModel(
            -1,
            "",
            "",
            WORKOUT_FUNCTION.WORKOUT,
            emptyList()
        )
        if (cursor == null || cursor.isBeforeFirst) return workout
        workout.mapRowToSelf(cursor)
        val workoutId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        val intervals = getIntervalsForWorkout(workoutId)
        workout.setInterval(intervals)
        return workout
    }
}