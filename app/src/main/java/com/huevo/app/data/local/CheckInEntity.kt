package com.huevo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.huevo.app.model.CheckInResult

/** Un registro por día: si el usuario mantuvo su compromiso o tuvo una caída. */
@Entity(tableName = "check_ins")
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val epochDay: Long,
    val result: CheckInResult,
    val createdAtMillis: Long
)
