package com.example.alarmled

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class AlarmsDataBaseHelper(
    context: Context?
) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "alarmled"
        const val DB_VERSION = 1

        fun insertAlarm(
            db: SQLiteDatabase,
            alarm: AlarmsFragment.Alarm
        ) {
            val alarmValues = ContentValues()
            alarmValues.put("HOUR", alarm.hour)
            alarmValues.put("MINUTE", alarm.minute)
            alarmValues.put("STATE", alarm.state)
            alarmValues.put("LIST_DAYS", alarm.listDays)
            alarmValues.put("REPEAT_MODE", alarm.repeatMode)
            alarmValues.put("RED", alarm.red)
            alarmValues.put("GREEN", alarm.green)
            alarmValues.put("BLUE", alarm.blue)


            db.insert("ALARM", null, alarmValues)
        }

        fun deleteAlarm(
            db: SQLiteDatabase,
            alarm: AlarmsFragment.Alarm
        ) {
            db.delete("ALARM", "_id = \"${alarm.id}\"",null)

        }

        fun updateAlarm(
            db: SQLiteDatabase,
            alarm: AlarmsFragment.Alarm
        ) {
            val alarmValues = ContentValues()
            alarmValues.put("HOUR", alarm.hour)
            alarmValues.put("MINUTE", alarm.minute)
            alarmValues.put("STATE", alarm.state)
            alarmValues.put("LIST_DAYS", alarm.listDays)
            alarmValues.put("REPEAT_MODE", alarm.repeatMode)
            alarmValues.put("RED", alarm.red)
            alarmValues.put("GREEN", alarm.green)
            alarmValues.put("BLUE", alarm.blue)

            db.update(
                "ALARM",
                alarmValues,
                "_id = \"${alarm.id}\"",
                arrayOf<String>()
            )

        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            " CREATE TABLE ALARM (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + " HOUR INT, "
                    + " MINUTE INT, "
                    + " STATE BOOLEAN, "
                    + " LIST_DAYS INT, "
                    + " REPEAT_MODE INT, "
                    + " RED INT, "
                    + " GREEN INT, "
                    + " BLUE INT);"

        )

        //insertAlarm(db!!,AlarmsFragment.Alarm(-1,12,14,true,2,5,5,5))
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}