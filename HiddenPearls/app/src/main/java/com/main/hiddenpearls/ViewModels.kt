package com.main.hiddenpearls.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch

import com.main.hiddenpearls.Location
import com.main.hiddenpearls.LocationCategory
import com.main.hiddenpearls.LocationService
import com.main.hiddenpearls.FavoritesService

sealed interface HomeUIState {
	data object Loading : HomeUIState
	data class Success(
		val pearls: List<Location>,
		val traps: List<Location>
	) : HomeUIState
	data class Error(val error: String?) : HomeUIState
}

sealed interface ListUIState {
	data object Loading : ListUIState
	data class Success(
		val locations: List<Location>
	) : ListUIState
	data class Error(val error: String?) : ListUIState
}

sealed interface DetailState {
	data object Loading : DetailState
	data class Success(
		val location: Location
	) : DetailState
	data class Error(val error: String?) : DetailState
}

class HomeViewModel() : ViewModel() {
	var uiState: HomeUIState by mutableStateOf(HomeUIState.Loading)
	private set

	init {
		getData()
	}

	private fun getData() {
		viewModelScope.launch {
			uiState = try {
				val pearls = LocationService.searchByCategory(LocationCategory.PEARL, 3)
				val traps = LocationService.searchByCategory(LocationCategory.TRAP, 3)
				HomeUIState.Success(pearls = pearls, traps = traps)
			} catch (e: Exception) {
				HomeUIState.Error(e.message)
			}
		}
	}
}

class ListViewModel() : ViewModel() {
	var uiState: ListUIState by mutableStateOf(ListUIState.Loading)
	private set

	init {
		getData()
	}

	private fun getData() {
		viewModelScope.launch {
			uiState = try {
				val locations = LocationService.getLocations()
				ListUIState.Success(locations)
			} catch (e: Exception) {
				ListUIState.Error(e.message)
			}
		}
	}
}

class FavoritesViewModel() : ViewModel() {
	var uiState: ListUIState by mutableStateOf(ListUIState.Loading)
	private set

	init {
		getData()
	}

	private fun getData() {
		viewModelScope.launch {
			uiState = try {
				val favorites = FavoritesService.getFavorites()
				if (favorites.size == 0) {
					ListUIState.Success(listOf())
				} else {
					val locations = LocationService.getLocations(favorites)
					ListUIState.Success(locations)
				}

			} catch (e: Exception) {
				ListUIState.Error(e.message)
			}
		}
	}
}

/* class DetailViewModel( */
/* 	savedStateHandle: SavedStateHandle */
/* ) : ViewModel() { */
/* 	var uiState: DetailState by mutableStateOf(DetailState.Loading) */
/* 	private set */

/* 	init { */
/* 		getData() */
/* 	} */

/* 	private fun getData() { */
/* 		viewModelScope.launch { */
/* 			uiState = try { */
/* 				val location = LocationService.searchById() */
/* 				DetailState.Success(location) */
/* 			} catch (e: Exception) { */
/* 				DetailState.Error(e.message) */
/* 			} */
/* 		} */
/* 	} */
/* } */
