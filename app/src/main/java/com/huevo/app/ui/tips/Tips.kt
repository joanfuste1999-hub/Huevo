package com.huevo.app.ui.tips

import java.time.LocalDate

object Tips {
    private val tips = listOf(
        "Cuando aparezca un impulso, cambia de entorno: levántate, camina o sal un momento.",
        "Bebe un vaso de agua y respira hondo tres veces antes de decidir nada.",
        "El aburrimiento es uno de los mayores disparadores. Ten siempre un plan B a mano.",
        "Dormir bien reduce mucho la aparición de impulsos nocturnos.",
        "Cada día que sumas fortalece tu disciplina, aunque no lo notes de inmediato.",
        "Si tienes una caída, no es el final. Es solo un nuevo comienzo.",
        "Mueve el cuerpo: 5 minutos de ejercicio pueden cortar un impulso a la mitad.",
        "Deja el móvil fuera de la habitación por la noche si puedes.",
        "Anota cómo te sientes. Ponerle nombre a la emoción le quita fuerza.",
        "Celebra tus pequeñas victorias. Un día a la vez es suficiente.",
        "La disciplina no es perfección, es volver a intentarlo cada vez que hace falta.",
        "Rodéate de actividades que te hagan sentir orgulloso al final del día."
    )

    fun tipForToday(): String {
        val index = LocalDate.now().dayOfYear % tips.size
        return tips[index]
    }
}
