package com.huevo.app.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.huevo.app.HuevoApplication
import com.huevo.app.model.Frequency
import com.huevo.app.model.Goal
import com.huevo.app.model.Moment
import com.huevo.app.model.OnboardingAnswers
import com.huevo.app.model.PornUsage
import com.huevo.app.model.Trigger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val ONBOARDING_TOTAL_STEPS = 11

data class OnboardingUiState(
    val step: Int = 1,
    val answers: OnboardingAnswers = OnboardingAnswers()
) {
    val canContinue: Boolean
        get() = when (step) {
            2 -> answers.name.isNotBlank()
            4 -> answers.frequency != null
            5 -> answers.goals.isNotEmpty()
            7 -> answers.pornUsage != null
            else -> true
        }
}

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as HuevoApplication).repository

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun goNext() {
        _uiState.update { it.copy(step = (it.step + 1).coerceAtMost(ONBOARDING_TOTAL_STEPS)) }
    }

    fun goBack() {
        _uiState.update { it.copy(step = (it.step - 1).coerceAtLeast(1)) }
    }

    fun updateName(name: String) {
        _uiState.update { it.copy(answers = it.answers.copy(name = name)) }
    }

    fun updateAge(age: Int) {
        _uiState.update { it.copy(answers = it.answers.copy(age = age)) }
    }

    fun selectFrequency(frequency: Frequency) {
        _uiState.update { it.copy(answers = it.answers.copy(frequency = frequency)) }
    }

    fun toggleGoal(goal: Goal) {
        _uiState.update {
            val current = it.answers.goals
            val updated = if (current.contains(goal)) current - goal else current + goal
            it.copy(answers = it.answers.copy(goals = updated))
        }
    }

    fun setInitialGoalDays(days: Int) {
        _uiState.update { it.copy(answers = it.answers.copy(initialGoalDays = days.coerceAtLeast(1))) }
    }

    fun selectPornUsage(usage: PornUsage) {
        _uiState.update { it.copy(answers = it.answers.copy(pornUsage = usage)) }
    }

    fun toggleMoment(moment: Moment) {
        _uiState.update {
            val current = it.answers.moments
            val updated = if (current.contains(moment)) current - moment else current + moment
            it.copy(answers = it.answers.copy(moments = updated))
        }
    }

    fun toggleTrigger(trigger: Trigger) {
        _uiState.update {
            val current = it.answers.triggers
            val updated = if (current.contains(trigger)) current - trigger else current + trigger
            it.copy(answers = it.answers.copy(triggers = updated))
        }
    }

    fun finishOnboarding(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.saveOnboardingAnswers(_uiState.value.answers)
            repository.setObjectiveDays(_uiState.value.answers.initialGoalDays)
            repository.completeOnboarding()
            onDone()
        }
    }
}
