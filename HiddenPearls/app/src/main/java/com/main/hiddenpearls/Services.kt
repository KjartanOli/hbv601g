package com.main.hiddenpearls

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import android.location.Location as GPSLocation

// service that fetches locations from the back-end api
object LocationService {
	val client: HttpClient = HttpClient {
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
			level = LogLevel.ALL
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

	suspend fun searchByName(name: String) : List<Location> {
		return client.get("locations?name=$name").body()
	}

	suspend fun searchByLocation(location: GPSLocation, radius: Double): List<Location> {
		Log.i("KÖTTUR","locations?longitude=${location.longitude}&latitude=${location.latitude}&radius=$radius")
		return client.get("locations?longitude=${location.longitude}&latitude=${location.latitude}&radius=$radius").body()
	}

	suspend fun random(): Location {
		return client.get("locations/random").body()
	}
}

// service to take care of storing and accessing favorites locations persistently
object FavoritesService {
	private lateinit var favoritesDataStore: FavoritesDataStore

	fun init(context: Context) {
		favoritesDataStore = FavoritesDataStore(context)
	}

	suspend fun getFavorites(): List<Long> {
		return favoritesDataStore.getAllFavoriteIds()
	}

	suspend fun addToFavorites(id: Long) {
		favoritesDataStore.addFavoriteId(id)
	}

	suspend fun removeFromFavorites(id: Long) {
		favoritesDataStore.removeFavoriteId(id)
	}

	suspend fun isFavorite(id: Long): Boolean {
		return favoritesDataStore.getAllFavoriteIds().contains(id)
	}
}
