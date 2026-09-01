package com.huevo.app.data

import com.huevo.app.data.local.CravingEntity
import com.huevo.app.model.Feeling
import com.huevo.app.model.Moment
import com.huevo.app.model.OnboardingAnswers
import java.time.Instant
import java.time.ZoneId

data class RiskMoment(val label: String, val score: Int)

data class PatternInsights(
    val hasEnoughData: Boolean,
    val riskMoments: List<RiskMoment>,
    val topFeeling: Feeling?,
    val advice: String
)

/**
 * Combina las respuestas del onboarding (base declarada por el usuario) con los registros
 * de impulso reales para estimar cuándo y por qué suelen aparecer los impulsos.
 */
object PatternAnalyzer {

    fun analyze(profile: OnboardingAnswers, cravings: List<CravingEntity>): PatternInsights {
        val scores = linkedMapOf<String, Int>()
        profile.moments.forEach { moment -> scores.merge(moment.label, 2) { a, b -> a + b } }

        cravings.forEach { craving ->
            val hour = Instant.ofEpochMilli(craving.timestampMillis).atZone(ZoneId.systemDefault()).hour
            val momentLabel = hourToMomentLabel(hour)
            scores.merge(momentLabel, 3) { a, b -> a + b }
        }

        val ranked = scores.entries
            .sortedByDescending { it.value }
            .take(4)
            .map { RiskMoment(it.key, it.value) }

        val topFeeling = cravings
            .groupingBy { it.feeling }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
            ?: profile.triggers.firstOrNull()?.let { mapTriggerToFeeling(it) }

        val advice = adviceFor(topFeeling, ranked.firstOrNull()?.label)

        return PatternInsights(
            hasEnoughData = cravings.isNotEmpty() || profile.moments.isNotEmpty(),
            riskMoments = ranked,
            topFeeling = topFeeling,
            advice = advice
        )
    }

    private fun hourToMomentLabel(hour: Int): String = when (hour) {
        in 0..4 -> Moment.BEFORE_SLEEP.label
        in 5..8 -> Moment.WAKING_UP.label
        in 21..23 -> Moment.NIGHT.label
        else -> Moment.BORED.label
    }

    private fun mapTriggerToFeeling(trigger: com.huevo.app.model.Trigger): Feeling = when (trigger) {
        com.huevo.app.model.Trigger.STRESS_ANXIETY -> Feeling.STRESS
        com.huevo.app.model.Trigger.BOREDOM -> Feeling.BOREDOM
        com.huevo.app.model.Trigger.SADNESS -> Feeling.STRESS
        com.huevo.app.model.Trigger.LONELINESS -> Feeling.LONELINESS
        com.huevo.app.model.Trigger.AFTER_PHONE -> Feeling.EXCITEMENT
        com.huevo.app.model.Trigger.EXPLICIT_CONTENT -> Feeling.EXCITEMENT
        com.huevo.app.model.Trigger.OTHER -> Feeling.OTHER
    }

    private fun adviceFor(feeling: Feeling?, topMoment: String?): String = when (feeling) {
        Feeling.STRESS -> "Cuando notes tensión, prueba una respiración de un minuto antes de coger el móvil. Ayuda más de lo que parece."
        Feeling.BOREDOM -> "El aburrimiento suele ser el disparador más común. Ten a mano una actividad corta para esos momentos muertos."
        Feeling.LONELINESS -> "Cuando aparezca la soledad, intenta escribir a alguien o salir un momento. Conectar ayuda a que el impulso pase."
        Feeling.ANXIETY -> "La ansiedad sube y baja como una ola. Dale un par de minutos antes de actuar por impulso."
        Feeling.EXCITEMENT -> "Intenta dejar el móvil fuera de la habitación por la noche. Reducir el estímulo reduce el impulso."
        Feeling.OTHER, null -> if (topMoment != null) {
            "Hemos detectado que $topMoment suele ser un momento de riesgo. Prepara con antelación qué harás en su lugar."
        } else {
            "Sigue registrando tus días para que podamos ofrecerte consejos más precisos."
        }
    }
}
