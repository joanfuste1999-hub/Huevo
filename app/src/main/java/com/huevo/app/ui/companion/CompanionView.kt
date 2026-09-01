package com.huevo.app.ui.companion

import android.graphics.Paint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.huevo.app.model.CompanionExpression
import com.huevo.app.model.CompanionStage
import kotlin.math.cos
import kotlin.math.sin

private val EggShell = Color(0xFFFFFBF4)
private val EggShellShadow = Color(0xFFF6E4C8)
private val ShellCrack = Color(0xFFE3A15C)
private val NestBrown = Color(0xFF9C7248)
private val NestBrownDark = Color(0xFF7A5636)
private val ChickYellow = Color(0xFFFFD666)
private val ChickYellowDark = Color(0xFFFFC53D)
private val ChickBelly = Color(0xFFFFF0C2)
private val BeakOrange = Color(0xFFFF7A1A)
private val FeetOrange = Color(0xFFE0670F)
private val InkBrown = Color(0xFF6A482A)
private val BlushPink = Color(0xFFFFB8A0)
private val SparkleGold = Color(0xFFFFC94D)

/**
 * Compañero virtual dibujado con Canvas: evoluciona de huevo a ave adulta a lo largo
 * de 6 etapas y expresa 7 estados de ánimo distintos mediante ojos, boca y pequeños detalles.
 */
@Composable
fun CompanionView(
    stage: CompanionStage,
    expression: CompanionExpression,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "companion_bob")
    val bob by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val bobOffsetPx = bob * size.height * 0.012f
        translate(top = bobOffsetPx) {
            when (stage) {
                CompanionStage.EGG -> drawEggStage(expression, cracked = false)
                CompanionStage.CRACKED_EGG -> drawEggStage(expression, cracked = true)
                CompanionStage.HATCHLING -> drawHatchlingStage(expression)
                CompanionStage.YOUNG_CHICK -> drawChickStage(expression, sparkleAlpha = twinkle)
                CompanionStage.TEEN_BIRD -> drawTeenStage(expression, sparkleAlpha = twinkle)
                CompanionStage.ADULT -> drawAdultStage(expression, sparkleAlpha = twinkle)
            }
        }
    }
}

private enum class EyeStyle { NORMAL, CLOSED, WIDE, HAPPY_ARC }
private enum class MouthStyle { SMILE, OPEN_O, FLAT, SMALL_OPEN, WAVY }
private enum class Decoration { NONE, ZZZ, SPARKLE, EXCLAMATION, SWEAT, ENERGY }

private data class FaceStyle(
    val eyes: EyeStyle,
    val mouth: MouthStyle,
    val blush: Boolean,
    val decoration: Decoration
)

private fun faceStyleFor(expression: CompanionExpression): FaceStyle = when (expression) {
    CompanionExpression.HAPPY -> FaceStyle(EyeStyle.HAPPY_ARC, MouthStyle.SMILE, true, Decoration.NONE)
    CompanionExpression.CURIOUS -> FaceStyle(EyeStyle.NORMAL, MouthStyle.SMALL_OPEN, false, Decoration.NONE)
    CompanionExpression.SLEEPY -> FaceStyle(EyeStyle.CLOSED, MouthStyle.FLAT, false, Decoration.ZZZ)
    CompanionExpression.SURPRISED -> FaceStyle(EyeStyle.WIDE, MouthStyle.OPEN_O, false, Decoration.EXCLAMATION)
    CompanionExpression.PROUD -> FaceStyle(EyeStyle.HAPPY_ARC, MouthStyle.SMILE, true, Decoration.SPARKLE)
    CompanionExpression.MOTIVATED -> FaceStyle(EyeStyle.NORMAL, MouthStyle.SMILE, false, Decoration.ENERGY)
    CompanionExpression.WORRIED -> FaceStyle(EyeStyle.NORMAL, MouthStyle.WAVY, false, Decoration.SWEAT)
}

// ---------- Etapa 1-2: Huevo ----------

private fun DrawScope.drawEggStage(expression: CompanionExpression, cracked: Boolean) {
    val w = size.width
    val h = size.height
    val cx = w / 2f

    drawNest(cx, h * 0.86f, w * 0.36f)

    val eggPath = Path().apply {
        moveTo(cx, h * 0.16f)
        cubicTo(cx + w * 0.30f, h * 0.20f, cx + w * 0.33f, h * 0.55f, cx + w * 0.26f, h * 0.72f)
        cubicTo(cx + w * 0.18f, h * 0.90f, cx - w * 0.18f, h * 0.90f, cx - w * 0.26f, h * 0.72f)
        cubicTo(cx - w * 0.33f, h * 0.55f, cx - w * 0.30f, h * 0.20f, cx, h * 0.16f)
        close()
    }
    drawPath(eggPath, color = EggShellShadow)
    drawPath(eggPath.let { shrinkPath(it, cx, h * 0.5f, 0.965f) }, color = EggShell)

    val faceCy = h * 0.52f
    val eyeGap = w * 0.10f
    val style = faceStyleFor(expression)
    drawFace(cx, faceCy, eyeGap, w * 0.10f, style, ink = InkBrown)

    if (cracked) {
        val crack = Path().apply {
            moveTo(cx - w * 0.20f, h * 0.42f)
            lineTo(cx - w * 0.08f, h * 0.36f)
            lineTo(cx, h * 0.44f)
            lineTo(cx + w * 0.09f, h * 0.35f)
            lineTo(cx + w * 0.20f, h * 0.41f)
        }
        drawPath(crack, color = ShellCrack, style = Stroke(width = w * 0.014f, cap = StrokeCap.Round))

        val tuft = Path().apply {
            moveTo(cx - w * 0.03f, h * 0.14f)
            quadraticBezierTo(cx, h * 0.03f, cx + w * 0.03f, h * 0.14f)
        }
        drawPath(tuft, color = ChickYellow, style = Stroke(width = w * 0.03f, cap = StrokeCap.Round))
    }
}

// ---------- Etapa 3: Pollito recién nacido (saliendo del huevo) ----------

private fun DrawScope.drawHatchlingStage(expression: CompanionExpression) {
    val w = size.width
    val h = size.height
    val cx = w / 2f

    drawNest(cx, h * 0.90f, w * 0.40f)

    // Mitad inferior de la cáscara, como una cuna
    val bottomShell = Path().apply {
        moveTo(cx - w * 0.28f, h * 0.62f)
        cubicTo(cx - w * 0.30f, h * 0.82f, cx - w * 0.16f, h * 0.92f, cx, h * 0.92f)
        cubicTo(cx + w * 0.16f, h * 0.92f, cx + w * 0.30f, h * 0.82f, cx + w * 0.28f, h * 0.62f)
        cubicTo(cx + w * 0.14f, h * 0.68f, cx - w * 0.14f, h * 0.68f, cx - w * 0.28f, h * 0.62f)
        close()
    }
    drawPath(bottomShell, color = EggShell)
    drawPath(bottomShell, color = EggShellShadow, style = Stroke(width = w * 0.008f))

    // Cuerpo del pollito asomando
    val bodyCy = h * 0.52f
    drawOval(color = ChickYellow, topLeft = Offset(cx - w * 0.22f, bodyCy - h * 0.19f), size = Size(w * 0.44f, h * 0.30f))

    // Ala superior de la cáscara, inclinada hacia atrás
    val topShell = Path().apply {
        moveTo(cx + w * 0.10f, h * 0.30f)
        cubicTo(cx + w * 0.30f, h * 0.20f, cx + w * 0.40f, h * 0.30f, cx + w * 0.32f, h * 0.44f)
        cubicTo(cx + w * 0.22f, h * 0.50f, cx + w * 0.10f, h * 0.44f, cx + w * 0.10f, h * 0.30f)
        close()
    }
    drawPath(topShell, color = EggShellShadow)
    drawPath(topShell, color = EggShell, style = Stroke(width = w * 0.01f))

    drawSmallWings(cx, bodyCy + h * 0.02f, w * 0.24f)
    drawBeak(cx, bodyCy - h * 0.02f, w * 0.05f)

    val style = faceStyleFor(expression)
    drawFace(cx, bodyCy - h * 0.06f, w * 0.085f, w * 0.095f, style, ink = InkBrown)
    drawDecoration(style.decoration, cx, h * 0.20f, w)
}

// ---------- Etapa 4: Pollito joven ----------

private fun DrawScope.drawChickStage(expression: CompanionExpression, sparkleAlpha: Float) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val groundY = h * 0.90f

    drawGroundShadow(cx, groundY, w * 0.26f)
    drawFeet(cx, groundY, w * 0.16f)

    val bodyTop = h * 0.34f
    val bodyBottom = h * 0.86f
    drawOval(color = ChickYellow, topLeft = Offset(cx - w * 0.26f, bodyTop), size = Size(w * 0.52f, bodyBottom - bodyTop))
    drawOval(
        color = ChickBelly,
        topLeft = Offset(cx - w * 0.16f, bodyTop + (bodyBottom - bodyTop) * 0.42f),
        size = Size(w * 0.32f, (bodyBottom - bodyTop) * 0.5f)
    )

    drawSmallWings(cx, bodyTop + (bodyBottom - bodyTop) * 0.42f, w * 0.30f)

    val headCy = bodyTop + h * 0.02f
    drawBeak(cx, headCy + h * 0.045f, w * 0.055f)
    val style = faceStyleFor(expression)
    drawFace(cx, headCy, w * 0.10f, w * 0.11f, style, ink = InkBrown)
    drawDecoration(style.decoration, cx, h * 0.14f, w, alpha = sparkleAlpha)
}

// ---------- Etapa 5: Ave joven ----------

private fun DrawScope.drawTeenStage(expression: CompanionExpression, sparkleAlpha: Float) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val groundY = h * 0.92f

    drawGroundShadow(cx, groundY, w * 0.30f)
    drawFeet(cx, groundY, w * 0.19f)

    // Cola
    val tail = Path().apply {
        moveTo(cx - w * 0.22f, h * 0.62f)
        quadraticBezierTo(cx - w * 0.40f, h * 0.58f, cx - w * 0.38f, h * 0.44f)
        quadraticBezierTo(cx - w * 0.24f, h * 0.52f, cx - w * 0.18f, h * 0.66f)
        close()
    }
    drawPath(tail, color = ChickYellowDark)

    val bodyTop = h * 0.28f
    val bodyBottom = h * 0.88f
    drawOval(color = ChickYellow, topLeft = Offset(cx - w * 0.29f, bodyTop), size = Size(w * 0.58f, bodyBottom - bodyTop))
    drawOval(
        color = ChickBelly,
        topLeft = Offset(cx - w * 0.17f, bodyTop + (bodyBottom - bodyTop) * 0.40f),
        size = Size(w * 0.34f, (bodyBottom - bodyTop) * 0.52f)
    )

    // Alas más definidas con plumas
    drawWingWithFeathers(cx - w * 0.30f, bodyTop + (bodyBottom - bodyTop) * 0.40f, w * 0.22f, mirrored = false)
    drawWingWithFeathers(cx + w * 0.30f, bodyTop + (bodyBottom - bodyTop) * 0.40f, w * 0.22f, mirrored = true)

    val headCy = bodyTop + h * 0.02f
    drawBeak(cx, headCy + h * 0.05f, w * 0.06f)
    val style = faceStyleFor(expression)
    drawFace(cx, headCy, w * 0.105f, w * 0.115f, style, ink = InkBrown)
    drawDecoration(style.decoration, cx, h * 0.10f, w, alpha = sparkleAlpha)
}

// ---------- Etapa 6: Evolución máxima ----------

private fun DrawScope.drawAdultStage(expression: CompanionExpression, sparkleAlpha: Float) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val groundY = h * 0.93f

    drawGroundShadow(cx, groundY, w * 0.34f)
    drawFeet(cx, groundY, w * 0.21f)

    val tail = Path().apply {
        moveTo(cx - w * 0.24f, h * 0.60f)
        quadraticBezierTo(cx - w * 0.46f, h * 0.54f, cx - w * 0.44f, h * 0.38f)
        quadraticBezierTo(cx - w * 0.26f, h * 0.46f, cx - w * 0.20f, h * 0.64f)
        close()
    }
    drawPath(tail, color = ChickYellowDark)

    val bodyTop = h * 0.22f
    val bodyBottom = h * 0.90f
    drawOval(color = ChickYellow, topLeft = Offset(cx - w * 0.31f, bodyTop), size = Size(w * 0.62f, bodyBottom - bodyTop))
    drawOval(
        color = ChickBelly,
        topLeft = Offset(cx - w * 0.18f, bodyTop + (bodyBottom - bodyTop) * 0.38f),
        size = Size(w * 0.36f, (bodyBottom - bodyTop) * 0.54f)
    )

    // Alas abiertas, en pose de celebración
    drawWingWithFeathers(cx - w * 0.32f, bodyTop + (bodyBottom - bodyTop) * 0.36f, w * 0.28f, mirrored = false, spread = true)
    drawWingWithFeathers(cx + w * 0.32f, bodyTop + (bodyBottom - bodyTop) * 0.36f, w * 0.28f, mirrored = true, spread = true)

    val headCy = bodyTop + h * 0.02f
    drawBeak(cx, headCy + h * 0.055f, w * 0.065f)
    val style = faceStyleFor(expression)
    drawFace(cx, headCy, w * 0.11f, w * 0.12f, style, ink = InkBrown)

    drawSparkleBurst(cx, h * 0.08f, w, sparkleAlpha)
    drawSparkleBurst(cx - w * 0.34f, h * 0.28f, w * 0.5f, sparkleAlpha)
    drawSparkleBurst(cx + w * 0.34f, h * 0.30f, w * 0.5f, sparkleAlpha)

    drawDecoration(style.decoration, cx, h * 0.06f, w, alpha = sparkleAlpha)
}

// ---------- Piezas reutilizables ----------

private fun DrawScope.drawNest(cx: Float, cy: Float, radiusX: Float) {
    val nest = Path().apply {
        moveTo(cx - radiusX, cy)
        quadraticBezierTo(cx, cy + radiusX * 0.55f, cx + radiusX, cy)
        quadraticBezierTo(cx, cy + radiusX * 0.32f, cx - radiusX, cy)
        close()
    }
    drawPath(nest, color = NestBrown)
    drawPath(nest, color = NestBrownDark, style = Stroke(width = radiusX * 0.05f))
}

private fun DrawScope.drawGroundShadow(cx: Float, cy: Float, radiusX: Float) {
    drawOval(
        color = InkBrown.copy(alpha = 0.12f),
        topLeft = Offset(cx - radiusX, cy - radiusX * 0.18f),
        size = Size(radiusX * 2f, radiusX * 0.36f)
    )
}

private fun DrawScope.drawFeet(cx: Float, groundY: Float, spread: Float) {
    val footHeight = spread * 0.4f
    listOf(cx - spread / 2f, cx + spread / 2f).forEach { footX ->
        drawLine(FeetOrange, Offset(footX, groundY - footHeight), Offset(footX, groundY), strokeWidth = spread * 0.12f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.drawBeak(cx: Float, cy: Float, halfWidth: Float) {
    val beak = Path().apply {
        moveTo(cx - halfWidth, cy)
        lineTo(cx + halfWidth, cy)
        lineTo(cx, cy + halfWidth * 1.3f)
        close()
    }
    drawPath(beak, color = BeakOrange)
}

private fun DrawScope.drawSmallWings(cx: Float, cy: Float, spread: Float) {
    listOf(-1f, 1f).forEach { side ->
        val wing = Path().apply {
            moveTo(cx + side * spread * 0.30f, cy - spread * 0.12f)
            quadraticBezierTo(cx + side * spread * 0.62f, cy, cx + side * spread * 0.32f, cy + spread * 0.28f)
            quadraticBezierTo(cx + side * spread * 0.22f, cy + spread * 0.10f, cx + side * spread * 0.30f, cy - spread * 0.12f)
            close()
        }
        drawPath(wing, color = ChickYellowDark)
    }
}

private fun DrawScope.drawWingWithFeathers(cx: Float, cy: Float, radius: Float, mirrored: Boolean, spread: Boolean = false) {
    val side = if (mirrored) 1f else -1f
    val lift = if (spread) -radius * 0.5f else 0f
    val wing = Path().apply {
        moveTo(cx, cy - radius * 0.3f)
        quadraticBezierTo(cx + side * radius * 1.1f, cy + lift, cx + side * radius * 0.85f, cy + radius * 0.7f)
        quadraticBezierTo(cx + side * radius * 0.35f, cy + radius * 0.55f, cx, cy + radius * 0.15f)
        close()
    }
    drawPath(wing, color = ChickYellowDark)
    repeat(3) { i ->
        val t = 0.35f + i * 0.22f
        val startX = cx + side * radius * 0.2f
        val startY = cy + radius * (0.1f + i * 0.18f)
        val endX = cx + side * radius * (0.75f + t * 0.2f)
        val endY = startY + radius * 0.28f + lift * 0.2f
        drawLine(BeakOrange.copy(alpha = 0.35f), Offset(startX, startY), Offset(endX, endY), strokeWidth = radius * 0.05f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.drawFace(cx: Float, cy: Float, eyeGap: Float, eyeSize: Float, style: FaceStyle, ink: Color) {
    val leftEyeX = cx - eyeGap
    val rightEyeX = cx + eyeGap

    if (style.blush) {
        drawCircle(BlushPink, radius = eyeSize * 0.75f, center = Offset(leftEyeX - eyeSize * 0.4f, cy + eyeSize * 1.15f))
        drawCircle(BlushPink, radius = eyeSize * 0.75f, center = Offset(rightEyeX + eyeSize * 0.4f, cy + eyeSize * 1.15f))
    }

    when (style.eyes) {
        EyeStyle.NORMAL -> {
            drawCircle(ink, radius = eyeSize * 0.4f, center = Offset(leftEyeX, cy))
            drawCircle(ink, radius = eyeSize * 0.4f, center = Offset(rightEyeX, cy))
            drawCircle(Color.White, radius = eyeSize * 0.13f, center = Offset(leftEyeX + eyeSize * 0.14f, cy - eyeSize * 0.14f))
            drawCircle(Color.White, radius = eyeSize * 0.13f, center = Offset(rightEyeX + eyeSize * 0.14f, cy - eyeSize * 0.14f))
        }
        EyeStyle.WIDE -> {
            drawCircle(ink, radius = eyeSize * 0.55f, center = Offset(leftEyeX, cy))
            drawCircle(ink, radius = eyeSize * 0.55f, center = Offset(rightEyeX, cy))
            drawCircle(Color.White, radius = eyeSize * 0.18f, center = Offset(leftEyeX + eyeSize * 0.18f, cy - eyeSize * 0.18f))
            drawCircle(Color.White, radius = eyeSize * 0.18f, center = Offset(rightEyeX + eyeSize * 0.18f, cy - eyeSize * 0.18f))
        }
        EyeStyle.CLOSED -> {
            val strokeW = eyeSize * 0.16f
            drawArc(ink, 20f, 140f, false, topLeft = Offset(leftEyeX - eyeSize * 0.4f, cy - eyeSize * 0.2f), size = Size(eyeSize * 0.8f, eyeSize * 0.6f), style = Stroke(strokeW, cap = StrokeCap.Round))
            drawArc(ink, 20f, 140f, false, topLeft = Offset(rightEyeX - eyeSize * 0.4f, cy - eyeSize * 0.2f), size = Size(eyeSize * 0.8f, eyeSize * 0.6f), style = Stroke(strokeW, cap = StrokeCap.Round))
        }
        EyeStyle.HAPPY_ARC -> {
            val strokeW = eyeSize * 0.18f
            drawArc(ink, 180f, 160f, false, topLeft = Offset(leftEyeX - eyeSize * 0.42f, cy - eyeSize * 0.35f), size = Size(eyeSize * 0.84f, eyeSize * 0.7f), style = Stroke(strokeW, cap = StrokeCap.Round))
            drawArc(ink, 180f, 160f, false, topLeft = Offset(rightEyeX - eyeSize * 0.42f, cy - eyeSize * 0.35f), size = Size(eyeSize * 0.84f, eyeSize * 0.7f), style = Stroke(strokeW, cap = StrokeCap.Round))
        }
    }

    val mouthY = cy + eyeSize * 1.05f
    val strokeW = eyeSize * 0.16f
    when (style.mouth) {
        MouthStyle.SMILE -> drawArc(ink, 20f, 140f, false, topLeft = Offset(cx - eyeSize * 0.55f, mouthY - eyeSize * 0.35f), size = Size(eyeSize * 1.1f, eyeSize * 0.7f), style = Stroke(strokeW, cap = StrokeCap.Round))
        MouthStyle.OPEN_O -> drawOval(ink, topLeft = Offset(cx - eyeSize * 0.22f, mouthY - eyeSize * 0.1f), size = Size(eyeSize * 0.44f, eyeSize * 0.5f))
        MouthStyle.SMALL_OPEN -> drawOval(ink, topLeft = Offset(cx - eyeSize * 0.16f, mouthY), size = Size(eyeSize * 0.32f, eyeSize * 0.22f))
        MouthStyle.FLAT -> drawLine(ink, Offset(cx - eyeSize * 0.28f, mouthY), Offset(cx + eyeSize * 0.28f, mouthY), strokeWidth = strokeW, cap = StrokeCap.Round)
        MouthStyle.WAVY -> {
            val wavy = Path().apply {
                moveTo(cx - eyeSize * 0.32f, mouthY)
                quadraticBezierTo(cx - eyeSize * 0.1f, mouthY - eyeSize * 0.18f, cx, mouthY)
                quadraticBezierTo(cx + eyeSize * 0.1f, mouthY + eyeSize * 0.18f, cx + eyeSize * 0.32f, mouthY)
            }
            drawPath(wavy, color = ink, style = Stroke(strokeW * 0.8f, cap = StrokeCap.Round))
        }
    }
}

private fun DrawScope.drawDecoration(decoration: Decoration, cx: Float, top: Float, w: Float, alpha: Float = 1f) {
    when (decoration) {
        Decoration.ZZZ -> drawText("Z z z", cx + w * 0.18f, top, w * 0.09f, InkBrown.copy(alpha = 0.6f))
        Decoration.EXCLAMATION -> drawText("!", cx + w * 0.20f, top, w * 0.16f, InkBrown)
        Decoration.SPARKLE -> drawStar(Offset(cx + w * 0.22f, top), w * 0.05f, SparkleGold.copy(alpha = alpha))
        Decoration.ENERGY -> {
            drawStar(Offset(cx - w * 0.24f, top + w * 0.02f), w * 0.035f, SparkleGold.copy(alpha = alpha))
            drawStar(Offset(cx + w * 0.24f, top - w * 0.01f), w * 0.045f, SparkleGold.copy(alpha = alpha))
        }
        Decoration.SWEAT -> {
            val drop = Path().apply {
                val dx = cx + w * 0.16f
                val dy = top + w * 0.02f
                moveTo(dx, dy)
                cubicTo(dx + w * 0.03f, dy + w * 0.04f, dx + w * 0.015f, dy + w * 0.08f, dx, dy + w * 0.08f)
                cubicTo(dx - w * 0.015f, dy + w * 0.08f, dx - w * 0.03f, dy + w * 0.04f, dx, dy)
                close()
            }
            drawPath(drop, color = Color(0xFF8FC7E8))
        }
        Decoration.NONE -> {}
    }
}

private fun DrawScope.drawText(text: String, x: Float, y: Float, textSizePx: Float, color: Color) {
    drawContext.canvas.nativeCanvas.drawText(
        text,
        x,
        y,
        Paint().apply {
            this.color = color.toArgb()
            this.textSize = textSizePx
            this.isAntiAlias = true
            this.isFakeBoldText = true
        }
    )
}

private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color) {
    val path = Path()
    val points = 4
    for (i in 0 until points * 2) {
        val angle = (Math.PI / points) * i - Math.PI / 2
        val r = if (i % 2 == 0) radius else radius * 0.4f
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = color)
}

private fun DrawScope.drawSparkleBurst(cx: Float, cy: Float, w: Float, alpha: Float) {
    drawStar(Offset(cx, cy), w * 0.04f, SparkleGold.copy(alpha = alpha))
}

private fun shrinkPath(path: Path, cx: Float, cy: Float, factor: Float): Path {
    val matrix = androidx.compose.ui.graphics.Matrix()
    matrix.translate(cx, cy)
    matrix.scale(factor, factor)
    matrix.translate(-cx, -cy)
    val out = Path()
    out.addPath(path)
    out.transform(matrix)
    return out
}
