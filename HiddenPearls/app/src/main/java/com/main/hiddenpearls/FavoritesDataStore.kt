package com.main.hiddenpearls

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

// Create the dataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

class FavoritesDataStore(context: Context) {
	private val dataStore = context.dataStore

	companion object {
		val FAVORITE_IDS_KEY = stringSetPreferencesKey("favorite_ids")
	}

	// Function to retrieve the list of favorite IDs
	private val favoriteIdsFlow: Flow<Set<String>> = dataStore.data
		.catch { exception ->
			// If there's an error reading preferences, emit an empty set
			if (exception is IOException) {
				emit(emptyPreferences())
			} else {
				throw exception
			}
		}
		.map { preferences ->
			// Get our set of favorite IDs, defaulting to an empty set if not set
			preferences[FAVORITE_IDS_KEY] ?: setOf()
		}

	suspend fun addFavoriteId(id: Long) {
		dataStore.edit { preferences ->
			val currentIds = preferences[FAVORITE_IDS_KEY] ?: setOf()
			preferences[FAVORITE_IDS_KEY] = currentIds + id.toString()
		}
	}

	suspend fun removeFavoriteId(id: Long) {
		dataStore.edit { preferences ->
			val currentIds = preferences[FAVORITE_IDS_KEY] ?: setOf()
			preferences[FAVORITE_IDS_KEY] = currentIds - id.toString()
		}
	}

	// Return a list of all favorited IDs
	suspend fun getAllFavoriteIds(): List<Long> {
		val idsSet = favoriteIdsFlow.first()
		return idsSet.map { it.toLong() }
	}
}
