package com.main.hiddenpearls

import android.location.Location as GPSLocation;

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import io.ktor.client.plugins.logging.*

object LocationService {
	val client: HttpClient = HttpClient(){
		install(ContentNegotiation) {
			json()
		}
		install(HttpCache)
		defaultRequest {
			url {
				/* protocol = URLProtocol.HTTP */
				/* host = "localhost" */
				/* port = 9090 */
				protocol = URLProtocol.HTTPS
				host = "hidden-pearls.onrender.com"
				path("api/")
			}
		}
		install(Logging) {
			logger = Logger.DEFAULT
			level = LogLevel.HEADERS
			sanitizeHeader { header -> header == HttpHeaders.Authorization }
		}
	}
	suspend fun getLocations(limit: Int? = null): List<Location> {
		return client.get("locations").body()
	}

	fun searchById(id: Long): Location {
		return Location(
			id = id,
			name = "Test Pearl",
			description = "A beautiful place, where no one goes",
			category = LocationCategory.PEARL,
			location = GPSLocation(""),
			statistics = listOf<VisitStatistic>()
		)
	}

	suspend fun searchByCategory(category: LocationCategory, limit: Int? = null): List<Location> {
		return client.get("locations?category=$category&limit=$limit").body()
	}

	fun searchByName(name: String) : List<Location> {
		return listOf(Location(
			id = 0,
			name = "Hafravatn",
			description = "Overlooked lake near the capitol area",
			category = LocationCategory.PEARL,
			location = GPSLocation(""),
			statistics = listOf<VisitStatistic>()
		))
	}

	fun random(): Location {
		return Location(
			id = 0,
			name = "Hafravatn",
			description = "Overlooked lake near the capitol area",
			category = LocationCategory.PEARL,
			location = GPSLocation(""),
			statistics = listOf<VisitStatistic>()
		)
	}
}
