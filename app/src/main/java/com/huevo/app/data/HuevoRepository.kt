package com.huevo.app.data

import android.content.Context
import com.huevo.app.data.local.CheckInEntity
import com.huevo.app.data.local.CravingEntity
import com.huevo.app.data.local.HuevoDatabase
import com.huevo.app.data.prefs.UserPreferencesRepository
import com.huevo.app.model.CheckInResult
import com.huevo.app.model.CompanionStage
import com.huevo.app.model.Feeling
import com.huevo.app.model.OnboardingAnswers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class HuevoRepository(context: Context) {

    private val db = HuevoDatabase.getInstance(context)
    private val checkInDao = db.checkInDao()
    private val cravingDao = db.cravingDao()
    private val prefs = UserPreferencesRepository(context)

    val onboardingAnswers: Flow<OnboardingAnswers> = prefs.onboardingAnswers
    val cravingLogs: Flow<List<CravingEntity>> = cravingDao.observeAll()

    val appState: Flow<AppState> = combine(
        prefs.onboardingAnswers,
        prefs.streakState,
        checkInDao.observeRecent(10)
    ) { profile, streak, recent ->
        val today = LocalDate.now().toEpochDay()
        val currentDay = (today - streak.streakStartEpochDay + 1).toInt().coerceAtLeast(1)
        AppState(
            profile = profile,
            currentStreakDays = currentDay,
            bestStreakDays = maxOf(streak.bestStreakDays, currentDay),
            objectiveDays = streak.objectiveDays,
            stage = CompanionStage.forDay(currentDay),
            alreadyCheckedInToday = streak.lastCheckInEpochDay == today,
            recentCheckIns = recent
        )
    }

    suspend fun saveOnboardingAnswers(answers: OnboardingAnswers) {
        prefs.saveOnboardingAnswers(answers)
    }

    suspend fun completeOnboarding() {
        prefs.completeOnboarding()
    }

    suspend fun logSuccessToday() {
        val today = LocalDate.now().toEpochDay()
        checkInDao.insert(
            CheckInEntity(epochDay = today, result = CheckInResult.SUCCESS, createdAtMillis = System.currentTimeMillis())
        )
        prefs.markCheckedInToday()
    }

    suspend fun logRelapseToday() {
        val today = LocalDate.now().toEpochDay()
        checkInDao.insert(
            CheckInEntity(epochDay = today, result = CheckInResult.RELAPSE, createdAtMillis = System.currentTimeMillis())
        )
        prefs.recordRelapseAndReset()
    }

    suspend fun logCraving(feeling: Feeling) {
        cravingDao.insert(CravingEntity(timestampMillis = System.currentTimeMillis(), feeling = feeling))
    }

    suspend fun setObjectiveDays(days: Int) {
        prefs.setObjectiveDays(days)
    }

    suspend fun eraseAllData() {
        checkInDao.clearAll()
        cravingDao.clearAll()
        prefs.clearAll()
    }
}
