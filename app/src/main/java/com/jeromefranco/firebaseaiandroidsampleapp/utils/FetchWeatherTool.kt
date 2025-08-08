package com.jeromefranco.firebaseaiandroidsampleapp.utils

import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.Schema
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Duration.Companion.seconds

data class Location(val city: String, val state: String)

suspend fun fetchWeather(location: Location, date: String): JsonObject {
    delay(2.seconds)

    // In a real app, you would call an external API here.
    return JsonObject(
        mapOf(
            "temperature" to JsonPrimitive(38),
            "chancePrecipitation" to JsonPrimitive("56%"),
            "cloudConditions" to JsonPrimitive("partlyCloudy")
        )
    )
}

val fetchWeatherTool = FunctionDeclaration(
    "fetchWeather",
    "Get the weather conditions for a specific city on a specific date.",
    mapOf(
        "location" to Schema.obj(
            mapOf(
                "city" to Schema.string("The city of the location."),
                "state" to Schema.string("The US state of the location."),
            ),
            description = "The name of the city and its state for which to get the weather. Only cities in the USA are supported."
        ),
        "date" to Schema.string(
            "The date for which to get the weather. Date must be in the format: YYYY-MM-DD."
        ),
    ),
)
