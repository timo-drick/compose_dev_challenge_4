package com.example.androiddevchallenge

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.androiddevchallenge.vec.Vec2
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin

private val sunGradient = listOf(Color.Yellow, Color.Yellow.copy(alpha = 0.3f))
private val coronaGradient = listOf(Color.Yellow.copy(alpha = 0.7f), Color.Yellow.copy(alpha = 0.0f))

fun DrawScope.Sun(seconds: Double) {
    val coronaIntensity = ((sin(seconds * 2.0) + 1.0) / 2.0).toFloat()
    val rCorona = .9f + 0.1f * coronaIntensity
    drawCircle(brush = Brush.radialGradient(coronaGradient, Offset.Zero, rCorona), rCorona, Offset.Zero)

    // Main sun
    val intensity = ((sin(seconds * 3.0) + 1.0) / 2.0).toFloat()
    val r = 0.5f + 0.1f * intensity
    drawCircle(brush = Brush.radialGradient(sunGradient, Offset.Zero, r), r, Offset.Zero)
}

fun DrawScope.SunnyWeather(seconds: Double, clouds: Int, wind: Float) {
    Sun(seconds)
    if (clouds > 0) {
        drawContext.canvas.save()
        drawContext.canvas.translate(0f, 0.4f)
        Clouds(seconds, clouds, wind, false)
        drawContext.canvas.restore()
    }
}

private val rainCloudGradient = listOf(Color.Gray, Color.Gray, Color.Gray.copy(alpha = 0.3f))
private val cloudGradient = listOf(Color.White, Color.White, Color.White.copy(alpha = 0.3f))

fun normalizeTime1(time: Double) = (time * 1000.0 % 1000.0) / 1000.0
fun normalizeTimeX(time: Double) = (((time * 1000.0 % 1000.0) / 1000.0) * 4.0 - 2.0).toFloat()

fun Vec2.toOffset(): Offset = Offset(x.toFloat(), y.toFloat())

fun DrawScope.Clouds(seconds: Double, clouds: Int, wind: Float, rain: Boolean) {
    val canvas = drawContext.canvas
    repeat(clouds) { index ->
        val n1 = noise1(index.toFloat() * 21)
        val n2 = noise1(index.toFloat() * 2452)
        val posX = normalizeTimeX(seconds * wind * 0.5 + n1)
        canvas.save()
        canvas.translate(posX, n2 * 0.5f)
        canvas.scale(0.6f, 0.6f)
        if (rain) {
            RainCloud(seconds, wind)
        } else {
            Cloud()
        }
        canvas.restore()
    }
}

fun DrawScope.Cloud() {
    val canvas = drawContext.canvas
    canvas.save()
    canvas.translate(0f, -1f)
    canvas.scale(1f, 0.5f)
    drawCircle(Brush.radialGradient(cloudGradient, Offset.Zero, 1f), 1f, Offset.Zero)
    canvas.restore()
}

fun DrawScope.RainCloud(seconds: Double, wind: Float) {
    repeat(100) { index ->
        val n1 = noise1(index.toFloat())
        val n2 = (noise1(index.toFloat() * 1434) * 0.9f) + 0.1f
        val time = seconds + n1 * 243
        val xOffset = n1 * 2f - 1f
        val speed = n2.pow(3f) * 0.9f + 0.1f
        Rain(time, xOffset, speed, wind)
    }
    val canvas = drawContext.canvas
    canvas.save()
    canvas.translate(0f, -1f)
    canvas.scale(1f, 0.5f)
    drawCircle(Brush.radialGradient(rainCloudGradient, Offset.Zero, 1f), 1f, Offset.Zero)
    canvas.restore()
}

fun DrawScope.Rain(seconds: Double, xOffset: Float, speed: Float, wind: Float)  {
    val yPos = (((seconds * 100.0 * speed) % 100) / 50f).toFloat()

    val direction = Vec2(2f * wind, 1f).normalized
    val xPos = (direction.x / direction.y) * yPos + xOffset
    val start = Vec2(xPos, yPos - 1f)
    val end = start + direction * (speed * 0.05f)
    drawLine(color = Color.Blue, start.toOffset(), end.toOffset(), strokeWidth = (0.01 * speed).toFloat())
}

fun smoothstep1(x: Float) = x * x * (3f - 2f * x)
fun mix(a: Float, b: Float, x: Float) = (1f - x) * a + x * b
fun fract(a: Float): Float = a - floor(a)

fun noise1(a: Float): Float = fract(sin(a * 100f) *5647f)
fun noise21(a: Float, b: Float): Float = fract(sin(a * 100f + b * 6574f) * 5647f)

fun fract(a: Double): Double = a - floor(a)
fun noise1(a: Double): Double = fract(sin(a * 100f) *5647f)

fun smoothNoise(time: Float): Float {
    val fract = fract(time)
    val id = floor(time)
    val lv = smoothstep1(fract)
    val n1 = noise1(id)
    val n2 = noise1(id + 1)
    return mix(n1, n2, lv)
}
fun smoothNoise21(time: Float, value: Float): Float {
    val fract = fract(time)
    val id = floor(time)
    val lv = smoothstep1(fract)
    val n1 = noise21(id, value)
    val n2 = noise21(id + 1, value)
    return mix(n1, n2, lv)
}

fun randomOffset(time: Float): Offset {
    return Offset.Zero
}
