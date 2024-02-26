package com.main.hiddenpearls.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

import com.main.hiddenpearls.Location
import com.main.hiddenpearls.LocationService
import com.main.hiddenpearls.ShakeForPearl
import com.main.hiddenpearls.viewModels.HomeViewModel
import com.main.hiddenpearls.viewModels.HomeUIState
import com.main.hiddenpearls.viewModels.ListViewModel
import com.main.hiddenpearls.viewModels.ListUIState

@Composable
fun HomeView(
	onNavigateToList: () -> Unit,
	onNavigateToDetails: (id: Long) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: HomeViewModel = viewModel(),
	navController: NavHostController,
) {
	ShakeForPearl(navController)
	val uiState = viewModel.uiState

	when (uiState) {
		is HomeUIState.Loading -> LoadingScreen()
		is HomeUIState.Success -> Column (
				modifier = Modifier
					.padding(12.dp)
					/* .verticalScroll(rememberScrollState()) */
			) {
				LocationList(
					heading = "Pearls",
					locations = uiState.pearls,
					onNavigateToDetails = onNavigateToDetails
				)
				LocationList(
					heading = "Traps",
					locations = uiState.traps,
					onNavigateToDetails = onNavigateToDetails
				)
			}
		is HomeUIState.Error -> ErrorScreen(uiState.error)
	}
}

@Composable
fun ListView(
	viewModel: ListViewModel = viewModel(),
	onNavigateToDetails: (id: Long) -> Unit,
	modifier: Modifier = Modifier,
) {
		val uiState = viewModel.uiState

		when (uiState) {
			is ListUIState.Loading -> LoadingScreen()
			is ListUIState.Success -> Column (
				modifier = Modifier
					.padding(12.dp)
					/* .verticalScroll(rememberScrollState()) */
			) {
				LocationList(
					heading = "Locations",
					locations = uiState.locations,
					onNavigateToDetails = onNavigateToDetails
				)
			}
			is ListUIState.Error -> ErrorScreen(uiState.error)
		}
}

@Composable
fun LocationList(
	heading: String,
	locations: List<Location>,
	onNavigateToDetails: (id: Long) -> Unit,
	modifier: Modifier = Modifier
) {
	Column (
		modifier = Modifier
		.padding(12.dp)
	) {
		Text(text = heading)
		Spacer(modifier = Modifier.height(12.dp))
		LazyColumn {
			items(locations) {
				location -> LocationCard(
					location = location,
					onNavigateToDetails = onNavigateToDetails
				)
			}
		}
	}
}

@Composable
fun LocationCard(
	location: Location,
	onNavigateToDetails: (id: Long) -> Unit
) {
	Column {
		// we can make the whole column into a clickable button, instead of having a dedicated one
		Text(text = location.name)
		Text(text = location.category.toString())
		Text(text = location.description)
		Button(onClick = { onNavigateToDetails(location.id) }) {
			Text(text = "See more")
		}
		Text(text = Json.encodeToString(location))
	}
}

@Composable
fun LocationDetails(
	location: Location,
	modifier: Modifier = Modifier
) {
	Column (modifier = Modifier
		.padding(12.dp)
	) {
		Text(text = location.name)
		Text(text = location.category.toString())
		Text(text = location.description)
		Text(text = Json.encodeToString(location))
	}
}

@Composable
fun LoadingScreen() {
	Text(text = "Loading…")
}

@Composable
fun ErrorScreen(error: String?) {
	if (error != null)
		Text(text = error)
	else
		Text(text = "Error!")
}
