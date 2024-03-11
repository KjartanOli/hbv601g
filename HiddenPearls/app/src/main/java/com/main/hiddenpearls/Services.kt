package com.main.hiddenpearls

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.cache.*
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import android.location.Location as GPSLocation

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

	suspend fun getLocations(ids: List<Long>): List<Location> {
		val ids = ids.joinToString(separator=",")
		return client.get("locations?ids=$ids").body()
	}

	suspend fun searchById(id: Long): Location {
		return client.get("locations/$id").body()
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

	fun searchByLocation(location: GPSLocation): List<Location> {
		return listOf(Location(
			id = 0,
			name = "Hafravatn",
			description = "Overlooked lake near the capitol area",
			category = LocationCategory.PEARL,
			location = GPSLocation(""),
			statistics = listOf<VisitStatistic>()
		))
	}

	suspend fun random(): Location {
		return client.get("locations/random").body()
	}
}

object FavoritesService {
	fun getFavorites(): List<Long> {
		return listOf(1,2,3)
	}

	fun addToFavorites(id: Long) {}
	fun removeFromFavorites(id: Long) {}
	fun isFavorite(id: Long): Boolean {
		return false
	}
}
