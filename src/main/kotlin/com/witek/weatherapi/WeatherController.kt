package com.witek.weatherapi

import com.witek.weatherapi.dto.CurrentWeatherDto
import feign.FeignException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class WeatherController(
    private val client: AccuWeatherClient,
    private val properties: AccuWeatherProperties
) {

    @GetMapping("/weather/{cityName}")
    fun read(@PathVariable cityName: String): CurrentWeatherDto {
        val locationKey: String = getLocationKey(cityName)

        return getCurrentWeather(locationKey)
    }

    @ExceptionHandler(FeignException::class)
    fun handleFeignException(ex: FeignException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Accuweather not available. Please try again later")
    }

    private fun getLocationKey(cityName: String): String =
        client
            .getLocationKey(apiKey = properties.apikey, cityName = cityName)
            .firstOrNull()?.key
            ?: error("Missing location key!")

    private fun getCurrentWeather(locationKey: String): CurrentWeatherDto =
        client.getCurrentConditions(locationKey = locationKey, apiKey = properties.apikey)
            .firstOrNull()
            ?: error("Unable to get current weather for $locationKey")
}