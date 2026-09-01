package com.huevo.app.data.local

import androidx.room.TypeConverter
import com.huevo.app.model.CheckInResult
import com.huevo.app.model.Feeling

class Converters {
    @TypeConverter
    fun fromCheckInResult(value: CheckInResult): String = value.name

    @TypeConverter
    fun toCheckInResult(value: String): CheckInResult = CheckInResult.valueOf(value)

    @TypeConverter
    fun fromFeeling(value: Feeling): String = value.name

    @TypeConverter
    fun toFeeling(value: String): Feeling = Feeling.valueOf(value)
}
