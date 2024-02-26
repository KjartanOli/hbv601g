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

sealed interface HomeUIState {
	data object Loading : HomeUIState
	data class Success(
		val pearls: List<Location>,
		val traps: List<Location>
	) : HomeUIState
	data class Error(val error: String?) : HomeUIState
}

class HomeViewModel(
	savedStateHandle: SavedStateHandle
) : ViewModel() {
	var uiState: HomeUIState by mutableStateOf(HomeUIState.Loading)
	private set

	init {
		getData()
	}

	private fun getData() {
		viewModelScope.launch {
			uiState = try {
				val pearls = LocationService.searchByCategory(LocationCategory.PEARL, 5)
				val traps = LocationService.searchByCategory(LocationCategory.TRAP, 5)
				HomeUIState.Success(pearls = pearls, traps = traps)
			} catch (e: Exception) {
				HomeUIState.Error(e.message)
			}
		}
	}
}
