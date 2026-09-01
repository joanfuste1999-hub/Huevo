package com.huevo.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.huevo.app.model.Feeling

/** Un evento de impulso registrado desde la sección Impulso, usado para detectar patrones. */
@Entity(tableName = "cravings")
data class CravingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestampMillis: Long,
    val feeling: Feeling
)
