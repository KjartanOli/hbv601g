package com.main.hiddenpearls.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.main.hiddenpearls.FavoritesService
import com.main.hiddenpearls.Location
import com.main.hiddenpearls.ShakeForPearl
import com.main.hiddenpearls.viewModels.DetailsState
import com.main.hiddenpearls.viewModels.DetailsViewModel
import com.main.hiddenpearls.viewModels.FavoritesViewModel
import com.main.hiddenpearls.viewModels.GPSSearchViewModel
import com.main.hiddenpearls.viewModels.GPSState
import com.main.hiddenpearls.viewModels.HomeUIState
import com.main.hiddenpearls.viewModels.HomeViewModel
import com.main.hiddenpearls.viewModels.ListUIState
import com.main.hiddenpearls.viewModels.ListViewModel
import com.main.hiddenpearls.viewModels.NameSearchViewModel
import com.main.hiddenpearls.viewModels.RandomViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
@Composable
fun HomeView(
	modifier: Modifier = Modifier,
	onNavigateToDetails: (id: Long) -> Unit,
	viewModel: HomeViewModel = viewModel(),
	navController: NavHostController,
) {
	ShakeForPearl(navController)
	when (val uiState = viewModel.uiState) {
		is HomeUIState.Loading -> LoadingScreen()
		is HomeUIState.Success -> Box {
			LazyColumn(modifier = Modifier.padding(12.dp)) {
				item {
					Text(text = "HIDDEN PEARLS", fontWeight = FontWeight.Bold, fontSize = 32.sp)
				}
				item {
					Spacer(modifier = Modifier.height(12.dp))
				}
				item {
					Text(text = "Top Pearls", fontWeight = FontWeight.Bold, fontSize = 24.sp)
				}
				items(uiState.pearls) { pearl ->
					LocationCard(
						location = pearl,
						onNavigateToDetails = onNavigateToDetails
					)
				}
				item {
					Text(text = "Worst Traps", fontWeight = FontWeight.Bold, fontSize = 24.sp)
				}
				items(uiState.traps) { trap ->
					LocationCard(
						location = trap,
						onNavigateToDetails = onNavigateToDetails
					)
				}
				item {
					Spacer(modifier = Modifier.height(100.dp))
				}
			}
		}
		is HomeUIState.Error -> ErrorScreen(uiState.error)
	}
}

@Composable
fun ListView(
	modifier: Modifier = Modifier,
	viewModel: ListViewModel = viewModel(),
	onNavigateToDetails: (id: Long) -> Unit,
) {
	when (val uiState = viewModel.uiState) {
		is ListUIState.Loading -> LoadingScreen()
		is ListUIState.Success -> Column(
			modifier = Modifier
				.padding(12.dp)
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
fun FavoritesView(
	modifier: Modifier = Modifier,
	onNavigateToDetails: (id: Long) -> Unit,
	viewModel: FavoritesViewModel = viewModel()
) {
	when (val uiState = viewModel.uiState) {
		is ListUIState.Loading -> LoadingScreen()
		is ListUIState.Success -> Column(
			modifier = Modifier
				.padding(12.dp)
		) {
			LocationList(
				heading = "Favourites",
				locations = uiState.locations,
				onNavigateToDetails = onNavigateToDetails
			)
		}
		is ListUIState.Error -> ErrorScreen(uiState.error)
	}
}

@Composable
fun DetailsView(
	modifier: Modifier = Modifier,
	viewModel: DetailsViewModel = viewModel(),
) {
	when (val uiState = viewModel.uiState) {
		is DetailsState.Loading -> LoadingScreen()
		is DetailsState.Success -> LocationDetails(uiState.location)
		is DetailsState.Error -> ErrorScreen(uiState.error)
	}
}

@Composable
fun RandomView(
	modifier: Modifier = Modifier,
	viewModel: RandomViewModel = viewModel(),
) {
	when (val uiState = viewModel.uiState) {
		is DetailsState.Loading -> LoadingScreen()
		is DetailsState.Success -> LocationDetails(uiState.location)
		is DetailsState.Error -> ErrorScreen(uiState.error)
	}
}

@Composable
fun NameSearchView(
	modifier: Modifier = Modifier,
	onNavigateToDetails: (id: Long) -> Unit,
	viewModel: NameSearchViewModel = viewModel(),
) {
	when (val uiState = viewModel.uiState) {
		is ListUIState.Loading -> LoadingScreen()
		is ListUIState.Success -> Column(
			modifier = Modifier
				.padding(12.dp)
		) {
			LocationList(
				heading = "Search Results",
				locations = uiState.locations,
				onNavigateToDetails = onNavigateToDetails
			)
		}
		is ListUIState.Error -> ErrorScreen(uiState.error)
	}
}

@Composable
fun GPSSearchView(
	modifier: Modifier = Modifier,
	onNavigateToDetails: (id: Long) -> Unit,
	viewModel: GPSSearchViewModel = viewModel()
) {
	when (val uiState = viewModel.uiState) {
		is GPSState.Loading -> LoadingScreen()
		is GPSState.Success -> Column(
			modifier = Modifier
				.padding(12.dp)
			/* .verticalScroll(rememberScrollState()) */
		) {
			LocationList(
				heading = "Search Results",
				locations = uiState.locations,
				onNavigateToDetails = onNavigateToDetails
			)
		}
		is GPSState.Error -> ErrorScreen(uiState.error)
	}
}

@Composable
fun LocationList(
	heading: String,
	locations: List<Location>,
	onNavigateToDetails: (id: Long) -> Unit,
) {
	Column(
		modifier = Modifier
			.padding(12.dp)
	) {
		Text(text = heading, fontWeight = FontWeight.Bold, fontSize = 24.sp)
		Spacer(modifier = Modifier.height(12.dp))
		LazyColumn {
			items(locations) { location ->
				LocationCard(
					location = location,
					onNavigateToDetails = onNavigateToDetails
				)
			}
			item {
				Spacer(modifier = Modifier.height(100.dp))
			}
		}
	}
}

@Composable
fun LocationCard(
	location: Location,
	onNavigateToDetails: (id: Long) -> Unit
) {
	Column(
		modifier = Modifier
			.clickable { onNavigateToDetails(location.id) }
			.fillMaxWidth()
			.clip(shape = RoundedCornerShape(5.dp))
			.padding(5.dp)
			.clip(RoundedCornerShape(5.dp))
			.background(
				MaterialTheme.colorScheme.primary,
				shape = RoundedCornerShape(5.dp)
			)
			.border(BorderStroke(3.dp, SolidColor(MaterialTheme.colorScheme.primary))),
	) {
		Text(
			text = location.name,
			color = MaterialTheme.colorScheme.onPrimary,
			modifier = Modifier
				.padding(horizontal = 10.dp, vertical = 3.dp)
		)
		Text(
			text = location.category.toString(),
			color = MaterialTheme.colorScheme.onPrimary,
			modifier = Modifier
				.padding(horizontal = 10.dp, vertical = 3.dp)
		)
		Text(
			text = location.description,
			color = MaterialTheme.colorScheme.onPrimary,
			modifier = Modifier
				.padding(horizontal = 10.dp, vertical = 3.dp)
		)
	}
}

@Composable
fun LocationDetails(location: Location) {
	val coroutineScope = rememberCoroutineScope()

	Column(
		modifier = Modifier
			.padding(12.dp)
	) {
		Text(
			text = location.name,
			fontWeight = FontWeight.Bold,
			fontSize = 24.sp
		)
		Text(text = location.category.toString())
		Text(text = location.description)
		Text(text = Json.encodeToString(location))

		val isFavorite = remember { mutableStateOf(false) }

		LaunchedEffect(location.id) {
			isFavorite.value = FavoritesService.isFavorite(location.id)
		}

		if (isFavorite.value) {
			Button(onClick = {
				coroutineScope.launch {
					FavoritesService.removeFromFavorites(location.id)
					isFavorite.value = false
				}
			})
			{
				Text(text = "Un-Favorite")
			}
		} else {
			Button(onClick = {
				coroutineScope.launch {
					FavoritesService.addToFavorites(location.id)
					isFavorite.value = true
				}
			})
			{
				Text(text = "Favorite")
			}
		}
	}
}

@Composable
fun LoadingScreen() {
	Box(
		modifier = Modifier
			.height(100.dp)
			.fillMaxWidth(),
		contentAlignment = Alignment.Center
	) {
		CircularProgressIndicator(
			modifier = Modifier.width(64.dp),
			color = MaterialTheme.colorScheme.secondary,
			trackColor = MaterialTheme.colorScheme.surfaceVariant,
		)
	}
}

@Composable
fun ErrorScreen(error: String?) {
	if (error != null)
		Text(text = error)
	else
		Text(text = "Error!")
}
