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
                "name VARCHAR(50)",
                "description VARCHAR(300)",
                "time INTEGER"
            ),
            "counted_interval" to listOf(
                "name VARCHAR(50)",
                "description VARCHAR(300)",
                "count INTEGER",
                "delay INTEGER"
            )
        )
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

    // Refactored to remove db parameter
    fun addWorkout(workout: WorkoutModel) {
        val workoutId = insertWorkout(workout)
        workout.intervals.forEach { rep ->
            insertInterval(rep, workoutId)
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
        model::class.members.filterIsInstance<KProperty1<WorkoutModel, *>>()
            .forEachIndexed { index, property ->
                val value = property.get(model)
                    when (value) {
                        is String -> statement.bindString(index + 1, value)
                        is Long -> statement.bindLong(index + 1, value)
                        is Double -> statement.bindDouble(index + 1, value)
                        else -> statement.bindString(
                            index + 1,
                            value.toString()
                        ) // Default to string if unknown type
                    }
            }
    }

    // Refactored to remove db parameter
    private fun insertInterval(rep: RepModel, workoutId: Long) {
        val db = writableDatabase // Get the writable database
        val columns = INTERVAL_TYPES[rep.mapsToTable]?.joinToString(", ") ?: ""
        val insertRep = """
            INSERT INTO ${rep.mapsToTable} ($columns, workout)
            VALUES (${INTERVAL_TYPES.get(rep.mapsToTable)}, ?);
        """
        val repStatement = db.compileStatement(insertRep)
        bindRepModelValuesToStatement(repStatement, rep)
        repStatement.bindLong(INTERVAL_TYPES.get(rep.mapsToTable)?.size?.plus(1) ?: 0, workoutId)
        repStatement.executeInsert()
    }

    private fun bindRepModelValuesToStatement(statement: SQLiteStatement, model: RepModel) {
        model::class.members.filterIsInstance<KProperty1<RepModel, *>>()
            .forEachIndexed { index, property ->
                val value = property.get(model)
                if (index == 0) {
                    when (value) {
                        is String -> statement.bindString(index + 1, value)
                        is Long -> statement.bindLong(index + 1, value)
                        is Double -> statement.bindDouble(index + 1, value)
                        else -> statement.bindString(
                            index + 1,
                            value.toString()
                        ) // Default to string if unknown type
                    }
                }
            }
    }

    // Refactored to remove db parameter
    fun updateWorkout(workoutId: Long, workout: WorkoutModel) {
        updateWorkoutInDb(workoutId, workout)
        workout.intervals.forEach { rep ->
            updateIntervalInDb(rep, workoutId)
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
    private fun updateIntervalInDb(rep: RepModel, workoutId: Long) {
        val db = writableDatabase // Get the writable database
        val updateRep = """
            UPDATE ${rep.mapsToTable}
            SET ${INTERVAL_TYPES[rep.mapsToTable]?.joinToString(", ") { "$it = ?" }}
            WHERE workout = ?;
        """
        val repStatement = db.compileStatement(updateRep)
        bindRepModelValuesToStatement(repStatement, rep)
        val intervalSize = INTERVAL_TYPES[rep.mapsToTable]?.size ?: 0
        repStatement.bindLong(intervalSize + 1, workoutId)
        repStatement.executeUpdateDelete()
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

    private fun mapRowToWorkout(cursor: Cursor?): WorkoutModel {
        if (cursor == null || cursor.isBeforeFirst) {
            return WorkoutModel(
                "",
                "",
                "",
                emptyList()
            ) // Return an empty workout model if cursor is empty or invalid
        }

        // Extract workout data from the cursor
        val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        val desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
        val function = cursor.getString(cursor.getColumnIndexOrThrow("plan_function"))

        // Get associated reps (intervals) for the workout
        val workoutId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        val intervals = getRepsForWorkout(workoutId)

        // Return the WorkoutModel populated with data
        return WorkoutModel(name, desc, function, intervals)
    }

    private fun getRepsForWorkout(workoutId: Int): List<RepModel> {
        val db = readableDatabase // Get the readable database
        val reps = mutableListOf<RepModel>()
        for (interval in INTERVAL_TYPES.keys) {
            val query = "SELECT * FROM $interval WHERE workout = ?"
            val cursor = db.rawQuery(query, arrayOf(workoutId.toString()))
            while (cursor.moveToNext()) {
                val repModel = mapRowToRep(cursor)
                reps.add(repModel)
            }
            cursor.close()
        }
        return reps
    }

    private fun mapRowToRep(cursor: Cursor?): RepModel {
        if (cursor == null || cursor.isBeforeFirst) {
            return RepModel(
                "",
                "",
                "",
                0,
                ""
            ) // Return an empty RepModel if the cursor is empty or invalid
        }

        // Extract interval (rep) data from the cursor
        val mapsToTable = cursor.getString(cursor.getColumnIndexOrThrow("mapsToTable"))
        val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
        val desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
        val value = cursor.getInt(cursor.getColumnIndexOrThrow("value"))
        try {
            val details = cursor.getString(cursor.getColumnIndexOrThrow("details"))
            return RepModel(mapsToTable, name, desc, value, details)
        }
        catch(e: IllegalArgumentException) {
            // assume there is no details
            return RepModel(mapsToTable, name, desc, value, "")
        }
    }
}
