package com.main.hiddenpearls.viewModels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.main.hiddenpearls.FavoritesService
import com.main.hiddenpearls.Location
import com.main.hiddenpearls.LocationCategory
import com.main.hiddenpearls.LocationService
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

sealed interface HomeUIState {
	data object Loading : HomeUIState
	data class Success(
		val pearls: List<Location>,
		val traps: List<Location>
	) : HomeUIState
	data class Error(val error: String? = null) : HomeUIState
}

sealed interface ListUIState {
	data object Loading : ListUIState
	data class Success(
		val locations: List<Location>
	) : ListUIState
	data class Error(val error: String? = null) : ListUIState
}

sealed interface DetailsState {
	data object Loading : DetailsState
	data class Success(
		val location: Location
	) : DetailsState
	data class Error(val error: String? = null) : DetailsState
}

sealed interface GPSState {
	data object Loading : GPSState
	data class Success(val locations: List<Location>) : GPSState
	data class Error(val error: String? = null) : GPSState
}

class HomeViewModel : ViewModel() {
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

class ListViewModel : ViewModel() {
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

class FavoritesViewModel : ViewModel() {
	var uiState: ListUIState by mutableStateOf(ListUIState.Loading)
	private set

	init {
		getData()
	}

	private fun getData() {
		viewModelScope.launch {
			uiState = try {
				val favorites = FavoritesService.getFavorites()
				if (favorites.isEmpty()) {
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

class DetailsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
	private val id = savedStateHandle.get<Long>("id")
	var uiState: DetailsState by mutableStateOf(DetailsState.Loading)
		private set

	init {
		getData()
	}

	private fun getData() {
		viewModelScope.launch {
			uiState = try {
				if (id == null) {
					DetailsState.Error()
				} else {
					val location = LocationService.searchById(id)
					DetailsState.Success(location)
				}
			} catch (e: Exception) {
				DetailsState.Error(e.message)
			}
		}
	}
}

class NameSearchViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
	private val search = savedStateHandle.get<String>("searchQuery")

	var uiState: ListUIState by mutableStateOf(ListUIState.Loading)
		private set

	init {
		getData()
	}

	private fun getData() {
		viewModelScope.launch {
			uiState = try {
				if (search == null) {
					ListUIState.Error()
				} else {
					val locations = LocationService.searchByName(search)
					ListUIState.Success(locations)
				}
			} catch (e: Exception) {
				ListUIState.Error(e.message)
			}
		}
	}
}

class RandomViewModel : ViewModel() {
	var uiState: DetailsState by mutableStateOf(DetailsState.Loading)
	private set

	init {
		getData()
	}

	private fun getData() {
		viewModelScope.launch {
			uiState = try {
				val location = LocationService.random()
				DetailsState.Success(location)
			} catch (e: Exception) {
				DetailsState.Error(e.message)
			}
		}
	}
}

class GPSSearchViewModelFactory(
	private val application: Application,
	private val radius: Double
) : ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(GPSSearchViewModel::class.java)) {
			return GPSSearchViewModel(application, radius) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}
}

class GPSSearchViewModel(
	application: Application,
	private val radius: Double
) : ViewModel() {
	var uiState: GPSState by mutableStateOf(GPSState.Loading)
		private set
	private val context: WeakReference<Application> = WeakReference(application)

	private val locationClient: FusedLocationProviderClient by lazy {
		LocationServices.getFusedLocationProviderClient(context.get()!!)
	}
	private var permission = false

 	init {
		 if (context.get()?.let {
				 ActivityCompat.checkSelfPermission(
					 it,
					 Manifest.permission.ACCESS_FINE_LOCATION
				 )
			 } == PackageManager.PERMISSION_GRANTED && context.get()?.let {
				 ActivityCompat.checkSelfPermission(
					 it,
					 Manifest.permission.ACCESS_COARSE_LOCATION
				 )
			 } == PackageManager.PERMISSION_GRANTED
		 ) {
			 permission = true
			 getData()
		 }
	 }


	@RequiresPermission(
		anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
	)
	private fun getData() {
		if (radius == null) {
			uiState = GPSState.Error("Radius not set")
			return
		}

		if (permission) {
			locationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
				if (location != null) {
					viewModelScope.launch {
						uiState = try {
							val locations = LocationService.searchByLocation(location, radius)
							GPSState.Success(locations)
						} catch (e: Exception) {
							GPSState.Error(e.message ?: "Unknown error")
						}
					}
				} else {
					uiState = GPSState.Error("Failed to get your current location")
				}
			}.addOnFailureListener { exception ->
				// Handle the failure here
				uiState = GPSState.Error(exception.message ?: "Unknown error")
			}
		} else {
			uiState = GPSState.Error("Location permission not granted")
		}

	}
}
