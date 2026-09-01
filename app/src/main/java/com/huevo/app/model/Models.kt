package com.huevo.app.model

/** Etapas de evolución del compañero, ligadas a los días de racha actual. */
enum class CompanionStage(val minDay: Int, val title: String, val subtitle: String) {
    EGG(1, "Huevo", "El comienzo de tu camino"),
    CRACKED_EGG(3, "Huevo agrietado", "Algo está a punto de pasar"),
    HATCHLING(7, "Pollito recién nacido", "¡Ha salido del cascarón!"),
    YOUNG_CHICK(14, "Pollito joven", "Cada vez más fuerte"),
    TEEN_BIRD(21, "Ave joven", "Casi lo consigues"),
    ADULT(30, "Evolución máxima", "Tu compañero ha crecido contigo");

    companion object {
        val ordered = entries.sortedBy { it.minDay }

        fun forDay(day: Int): CompanionStage =
            ordered.lastOrNull { day >= it.minDay } ?: EGG

        /** Siguiente hito de evolución, o null si ya está en la etapa máxima. */
        fun nextMilestone(day: Int): CompanionStage? =
            ordered.firstOrNull { day < it.minDay }
    }
}

enum class CompanionExpression {
    HAPPY, CURIOUS, SLEEPY, SURPRISED, PROUD, MOTIVATED, WORRIED
}

enum class CheckInResult { SUCCESS, RELAPSE }

/** Cómo se siente el usuario cuando visita la sección Impulso. */
enum class Feeling(val label: String) {
    STRESS("Estrés"),
    BOREDOM("Aburrimiento"),
    LONELINESS("Soledad"),
    ANXIETY("Ansiedad"),
    EXCITEMENT("Excitación"),
    OTHER("Otro")
}

enum class Frequency(val label: String) {
    DAILY("Todos los días"),
    SEVERAL_PER_WEEK("Varias veces por semana"),
    WEEKLY("1 vez por semana"),
    EVERY_2_3_WEEKS("Cada 2-3 semanas"),
    LESS_THAN_MONTHLY("Menos de 1 vez al mes")
}

enum class Goal(val label: String) {
    QUIT_COMPLETELY("Dejarlo por completo"),
    REDUCE_FREQUENCY("Reducir la frecuencia"),
    REGAIN_CONTROL("Recuperar el control"),
    IMPROVE_DISCIPLINE("Mejorar mi disciplina"),
    OTHER("Otro objetivo")
}

enum class PornUsage(val label: String) {
    OFTEN("Sí, a menudo"),
    SOMETIMES("Sí, algunas veces"),
    RARELY("No, rara vez"),
    NEVER("No, nunca")
}

enum class Moment(val label: String) {
    NIGHT("Por la noche"),
    BEFORE_SLEEP("Antes de dormir"),
    BORED("Cuando estoy aburrido"),
    ALONE("Cuando estoy solo"),
    STRESSED("Cuando estoy estresado"),
    WAKING_UP("Al despertarme"),
    OTHER("Otro momento")
}

enum class Trigger(val label: String) {
    STRESS_ANXIETY("Estrés / Ansiedad"),
    BOREDOM("Aburrimiento"),
    SADNESS("Tristeza / Bajón emocional"),
    LONELINESS("Soledad"),
    AFTER_PHONE("Después de usar el móvil"),
    EXPLICIT_CONTENT("Ver cierto contenido"),
    OTHER("Otra situación")
}

data class OnboardingAnswers(
    val name: String = "",
    val age: Int = 25,
    val frequency: Frequency? = null,
    val goals: Set<Goal> = emptySet(),
    val initialGoalDays: Int = 30,
    val pornUsage: PornUsage? = null,
    val moments: Set<Moment> = emptySet(),
    val triggers: Set<Trigger> = emptySet(),
    val completed: Boolean = false
)

data class CheckInLog(
    val id: Long = 0,
    val epochDay: Long,
    val result: CheckInResult
)

data class CravingLog(
    val id: Long = 0,
    val timestampMillis: Long,
    val feeling: Feeling
)
