package com.huevo.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.huevo.app.model.Frequency
import com.huevo.app.model.Goal
import com.huevo.app.model.Moment
import com.huevo.app.model.OnboardingAnswers
import com.huevo.app.model.PornUsage
import com.huevo.app.model.Trigger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "huevo_prefs")

data class StreakState(
    val streakStartEpochDay: Long,
    val bestStreakDays: Int,
    val objectiveDays: Int,
    val lastCheckInEpochDay: Long?
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val NAME = stringPreferencesKey("name")
        val AGE = intPreferencesKey("age")
        val FREQUENCY = stringPreferencesKey("frequency")
        val GOALS = stringSetPreferencesKey("goals")
        val INITIAL_GOAL_DAYS = intPreferencesKey("initial_goal_days")
        val PORN_USAGE = stringPreferencesKey("porn_usage")
        val MOMENTS = stringSetPreferencesKey("moments")
        val TRIGGERS = stringSetPreferencesKey("triggers")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

        val STREAK_START_EPOCH_DAY = longPreferencesKey("streak_start_epoch_day")
        val BEST_STREAK_DAYS = intPreferencesKey("best_streak_days")
        val OBJECTIVE_DAYS = intPreferencesKey("objective_days")
        val LAST_CHECK_IN_EPOCH_DAY = longPreferencesKey("last_check_in_epoch_day")
    }

    val onboardingAnswers: Flow<OnboardingAnswers> = context.dataStore.data.map { prefs ->
        OnboardingAnswers(
            name = prefs[Keys.NAME] ?: "",
            age = prefs[Keys.AGE] ?: 25,
            frequency = prefs[Keys.FREQUENCY]?.let { runCatching { Frequency.valueOf(it) }.getOrNull() },
            goals = prefs[Keys.GOALS]?.mapNotNull { runCatching { Goal.valueOf(it) }.getOrNull() }?.toSet() ?: emptySet(),
            initialGoalDays = prefs[Keys.INITIAL_GOAL_DAYS] ?: 30,
            pornUsage = prefs[Keys.PORN_USAGE]?.let { runCatching { PornUsage.valueOf(it) }.getOrNull() },
            moments = prefs[Keys.MOMENTS]?.mapNotNull { runCatching { Moment.valueOf(it) }.getOrNull() }?.toSet() ?: emptySet(),
            triggers = prefs[Keys.TRIGGERS]?.mapNotNull { runCatching { Trigger.valueOf(it) }.getOrNull() }?.toSet() ?: emptySet(),
            completed = prefs[Keys.ONBOARDING_COMPLETED] ?: false
        )
    }

    val streakState: Flow<StreakState> = context.dataStore.data.map { prefs ->
        val today = LocalDate.now().toEpochDay()
        StreakState(
            streakStartEpochDay = prefs[Keys.STREAK_START_EPOCH_DAY] ?: today,
            bestStreakDays = prefs[Keys.BEST_STREAK_DAYS] ?: 0,
            objectiveDays = prefs[Keys.OBJECTIVE_DAYS] ?: 30,
            lastCheckInEpochDay = prefs[Keys.LAST_CHECK_IN_EPOCH_DAY]
        )
    }

    suspend fun saveOnboardingAnswers(answers: OnboardingAnswers) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NAME] = answers.name
            prefs[Keys.AGE] = answers.age
            answers.frequency?.let { prefs[Keys.FREQUENCY] = it.name }
            prefs[Keys.GOALS] = answers.goals.map { it.name }.toSet()
            prefs[Keys.INITIAL_GOAL_DAYS] = answers.initialGoalDays
            answers.pornUsage?.let { prefs[Keys.PORN_USAGE] = it.name }
            prefs[Keys.MOMENTS] = answers.moments.map { it.name }.toSet()
            prefs[Keys.TRIGGERS] = answers.triggers.map { it.name }.toSet()
        }
    }

    suspend fun completeOnboarding() {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = true
            if (prefs[Keys.STREAK_START_EPOCH_DAY] == null) {
                prefs[Keys.STREAK_START_EPOCH_DAY] = LocalDate.now().toEpochDay()
            }
            val initialGoal = prefs[Keys.INITIAL_GOAL_DAYS] ?: 30
            if (prefs[Keys.OBJECTIVE_DAYS] == null) {
                prefs[Keys.OBJECTIVE_DAYS] = initialGoal
            }
        }
    }

    suspend fun markCheckedInToday() {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_CHECK_IN_EPOCH_DAY] = LocalDate.now().toEpochDay()
        }
    }

    suspend fun recordRelapseAndReset() {
        val today = LocalDate.now().toEpochDay()
        context.dataStore.edit { prefs ->
            val start = prefs[Keys.STREAK_START_EPOCH_DAY] ?: today
            val currentStreak = (today - start + 1).toInt().coerceAtLeast(0)
            val best = prefs[Keys.BEST_STREAK_DAYS] ?: 0
            if (currentStreak > best) prefs[Keys.BEST_STREAK_DAYS] = currentStreak
            prefs[Keys.STREAK_START_EPOCH_DAY] = today
            prefs[Keys.LAST_CHECK_IN_EPOCH_DAY] = today
        }
    }

    suspend fun setObjectiveDays(days: Int) {
        context.dataStore.edit { prefs -> prefs[Keys.OBJECTIVE_DAYS] = days }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}
