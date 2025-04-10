package com.witek.weatherapi

import com.witek.weatherapi.dto.CurrentWeatherResponse
import feign.FeignException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
private class WeatherController(
    private val service: AccuWeatherService
) {

    @GetMapping("/weather/{cityName}")
    @ResponseStatus(HttpStatus.OK)
    fun read(@PathVariable cityName: String): CurrentWeatherResponse {
        return service.getCurrentWeather(cityName)
    }

    @ExceptionHandler(FeignException::class)
    fun handleFeignException(ex: FeignException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Accuweather not available. Please try again later")
    }
}