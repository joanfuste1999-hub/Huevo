package com.huevo.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.huevo.app.HuevoApplication
import com.huevo.app.data.AppState
import com.huevo.app.data.PatternAnalyzer
import com.huevo.app.data.PatternInsights
import com.huevo.app.model.Feeling
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as HuevoApplication).repository

    val appState: StateFlow<AppState> = repository.appState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppState.Loading
    )

    val patternInsights: StateFlow<PatternInsights> = combine(
        repository.onboardingAnswers,
        repository.cravingLogs
    ) { profile, cravings -> PatternAnalyzer.analyze(profile, cravings) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PatternAnalyzer.analyze(com.huevo.app.model.OnboardingAnswers(), emptyList())
    )

    fun checkInSuccess() {
        viewModelScope.launch { repository.logSuccessToday() }
    }

    fun checkInRelapse() {
        viewModelScope.launch { repository.logRelapseToday() }
    }

    fun logCraving(feeling: Feeling) {
        viewModelScope.launch { repository.logCraving(feeling) }
    }

    fun setObjectiveDays(days: Int) {
        viewModelScope.launch { repository.setObjectiveDays(days) }
    }

    fun eraseAllData(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.eraseAllData()
            onDone()
        }
    }
}
