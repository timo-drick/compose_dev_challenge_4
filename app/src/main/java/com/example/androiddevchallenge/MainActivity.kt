/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.isActive

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        ForecastView()
    }
}

enum class CloudCover(val description: String) {
    CLEAR("Clear"), CLOUDY_1("Single clouds"), CLOUDY_2("Cloudy"), CLOUDY_3("Very cloudy"), OVERCAST("Overcast");
}

data class Weather(val day: String, val clouds: CloudCover, val windSpeedKmh: Int, val rain: Boolean)

@OptIn(ExperimentalFoundationApi::class)
@Preview("Canvas", widthDp = 360, heightDp = 640)
@Composable
fun ForecastView() {
    val test = 10
    val weatherForecast = listOf(
        Weather("Monday", CloudCover.CLOUDY_2, 10, false),
        Weather("Tuesday", CloudCover.CLOUDY_3, 30, true),
        Weather("Wednesday", CloudCover.CLEAR, 10, false),
        Weather("Thursday", CloudCover.CLOUDY_1, 60, false),
        Weather("Friday", CloudCover.OVERCAST, 100, true),
    )

    var seconds by remember { mutableStateOf(0.0) }
    LaunchedEffect(key1 = test) {
        val startMillis = withFrameMillis { it }
        while (isActive) {
            withInfiniteAnimationFrameMillis {
                val duration = it - startMillis
                seconds = duration.toDouble() / 1000.0
            }
        }
    }
    LazyColumn(Modifier.background(Color.White).fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stickyHeader {
            Box(Modifier.fillMaxWidth().background(Color.White.copy(alpha = 0.8f)).padding(8.dp)) {
                Text(
                    "Weather forecast of the next days",
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        items(weatherForecast) { weather ->
            Box(Modifier.clip(RoundedCornerShape(8.dp))) {
                Row {
                    Column(Modifier.width(120.dp)) {
                        Text(weather.day)
                        Text(weather.clouds.description)
                        Text("Wind: ${weather.windSpeedKmh} Km/h")
                        if (weather.rain) Text("Rainy")
                    }
                    Box(Modifier.weight(1f)) {
                        WeatherCanvas(
                            Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)),
                            seconds = seconds,
                            weather = weather
                        )
                    }
                }
            }
        }
    }
}

val skyColor = Color(0xFF9FF3FF)
val darkSkyColor = Color(0xFFC0DDE2)

@Composable
fun WeatherCanvas(modifier: Modifier, seconds: Double, weather: Weather) {
    val clouds = when (weather.clouds) {
        CloudCover.CLEAR -> 0
        CloudCover.CLOUDY_1 -> 1
        CloudCover.CLOUDY_2 -> 2
        CloudCover.CLOUDY_3 -> 3
        CloudCover.OVERCAST -> 4
    }
    val wind = (weather.windSpeedKmh.toFloat() / 150f).coerceIn(0.1f, 1f)
    val sunnyWeather = clouds < 3 && weather.rain.not()
    Canvas(
        modifier = modifier.background(if (sunnyWeather) skyColor else darkSkyColor)
    ) {
        val canvas = drawContext.canvas
        val scale = size.minDimension / 2f
        canvas.translate(center.x, center.y)
        canvas.scale(scale, scale)
        if (sunnyWeather) {
            SunnyWeather(seconds, clouds, wind)
        } else {
            Clouds(seconds, clouds, wind, weather.rain)
        }
    }
}

/*@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
*/
