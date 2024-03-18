package com.main.hiddenpearls.viewModels

import android.location.Location as GPSLocation
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import android.Manifest
import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.main.hiddenpearls.FavoritesService
import com.main.hiddenpearls.Location
import com.main.hiddenpearls.LocationCategory
import com.main.hiddenpearls.LocationService
import kotlinx.coroutines.launch

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

class RandomViewModel() : ViewModel() {
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

class GPSSearchViewModel(application: Application) : AndroidViewModel(application) {
	var uiState: GPSState by mutableStateOf(GPSState.Loading)
		private set
	private val context = getApplication<Application>()

	private val locationClient = LocationServices.getFusedLocationProviderClient(context)
 	//val locationClient = remember {
        //LocationServices.getFusedLocationProviderClient(context)
     //}

 	init {
		 if (hasLocationPermission()) {
			 if (ActivityCompat.checkSelfPermission(
					 context,
					 Manifest.permission.ACCESS_FINE_LOCATION
				 ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
					 context,
					 Manifest.permission.ACCESS_COARSE_LOCATION
				 ) != PackageManager.PERMISSION_GRANTED
			 ) {
				 // TODO: Consider calling
				 //    ActivityCompat#requestPermissions
				 // here to request the missing permissions, and then overriding
				 //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				 //                                          int[] grantResults)
				 // to handle the case where the user grants the permission. See the documentation
				 // for ActivityCompat#requestPermissions for more details.
				 getData()
			 }
		 }
	}

 	@RequiresPermission(
 		anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
 	)
 	private fun getData() {
 		viewModelScope.launch {
			 if (hasLocationPermission()) {
				 locationClient.lastLocation.addOnSuccessListener { location: GPSLocation? ->
					 uiState = if (location == null) {
						 GPSState.Error("Failed to get your current location")
					 } else {
						 val locations = LocationService.searchByLocation(location)
						 GPSState.Success(locations)
					 }
				 }

			 } else {
				 uiState = GPSState.Error("Location permission not granted")
 			}
 		}
 	}

	private fun hasLocationPermission(): Boolean {
		return ContextCompat.checkSelfPermission(
			context,
			Manifest.permission.ACCESS_FINE_LOCATION
		) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
			context,
			Manifest.permission.ACCESS_COARSE_LOCATION
		) == PackageManager.PERMISSION_GRANTED
	}
}
