package com.jway.stepwake

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class AlarmDbHelper(context: Context) : android.database.sqlite.SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE Alarms (id INTEGER PRIMARY KEY AUTOINCREMENT, hour INTEGER, minute INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Alarms")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "alarms.db"
        const val DATABASE_VERSION = 1
    }
}