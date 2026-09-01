package com.huevo.app.data

import com.huevo.app.data.local.CheckInEntity
import com.huevo.app.model.CompanionStage
import com.huevo.app.model.OnboardingAnswers

data class AppState(
    val profile: OnboardingAnswers,
    val currentStreakDays: Int,
    val bestStreakDays: Int,
    val objectiveDays: Int,
    val stage: CompanionStage,
    val alreadyCheckedInToday: Boolean,
    val recentCheckIns: List<CheckInEntity>
) {
    val progressToNextStageLabel: String
        get() {
            val next = CompanionStage.nextMilestone(currentStreakDays)
            return if (next != null) {
                "$currentStreakDays / ${next.minDay} días"
            } else {
                "Evolución máxima alcanzada"
            }
        }

    val progressToNextStageFraction: Float
        get() {
            val next = CompanionStage.nextMilestone(currentStreakDays) ?: return 1f
            val prevThreshold = stage.minDay
            val span = (next.minDay - prevThreshold).coerceAtLeast(1)
            val advanced = (currentStreakDays - prevThreshold).coerceAtLeast(0)
            return (advanced.toFloat() / span.toFloat()).coerceIn(0f, 1f)
        }

    val objectiveProgressFraction: Float
        get() = (currentStreakDays.toFloat() / objectiveDays.toFloat()).coerceIn(0f, 1f)

    val daysRemainingToObjective: Int
        get() = (objectiveDays - currentStreakDays).coerceAtLeast(0)

    companion object {
        val Loading = AppState(
            profile = OnboardingAnswers(),
            currentStreakDays = 1,
            bestStreakDays = 0,
            objectiveDays = 30,
            stage = CompanionStage.EGG,
            alreadyCheckedInToday = false,
            recentCheckIns = emptyList()
        )
    }
}
