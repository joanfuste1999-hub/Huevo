package com.huevo.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CheckInEntity::class, CravingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HuevoDatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao
    abstract fun cravingDao(): CravingDao

    companion object {
        @Volatile private var instance: HuevoDatabase? = null

        fun getInstance(context: Context): HuevoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    HuevoDatabase::class.java,
                    "huevo.db"
                ).build().also { instance = it }
            }
    }
}
