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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.main.hiddenpearls.Location
import com.main.hiddenpearls.ShakeForPearl
import com.main.hiddenpearls.viewModels.FavoritesViewModel
import com.main.hiddenpearls.viewModels.DetailsViewModel
import com.main.hiddenpearls.viewModels.HomeUIState
import com.main.hiddenpearls.viewModels.HomeViewModel
import com.main.hiddenpearls.viewModels.ListUIState
import com.main.hiddenpearls.viewModels.DetailsState
import com.main.hiddenpearls.viewModels.ListViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun HomeView(
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
				Text(text = "HIDDEN PEARLS", fontWeight = FontWeight.Bold, fontSize = 32.sp)

				LocationList(
					heading = "Best Pearls",
					locations = uiState.pearls,
					onNavigateToDetails = onNavigateToDetails
				)
				LocationList(
					heading = "Worst Traps",
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
fun FavoritesView(
	onNavigateToDetails: (id: Long) -> Unit,
	viewModel: FavoritesViewModel = viewModel(),
	modifier: Modifier = Modifier
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
	viewModel: DetailsViewModel = viewModel(),
	modifier: Modifier = Modifier
) {
	val uiState = viewModel.uiState

	when (uiState) {
		is DetailsState.Loading -> LoadingScreen()
		is DetailsState.Success -> 	Column (modifier = Modifier
		.padding(12.dp)
		) {
			Text(text = uiState.location.name,
				fontWeight = FontWeight.Bold,
				fontSize = 24.sp)
			Text(text = uiState.location.category.toString())
			Text(text = uiState.location.description)
			Text(text = Json.encodeToString(uiState.location))
			Button(onClick = { /*TODO: Call favorite/unfavorite*/ }) {

			}
		}
		is DetailsState.Error -> ErrorScreen(uiState.error)
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
		Text(text = heading, fontWeight = FontWeight.Bold, fontSize = 24.sp)
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
	Column (modifier = Modifier
		.clickable { onNavigateToDetails(location.id) }
		.fillMaxWidth()
		.clip(shape = RoundedCornerShape(5.dp))
		.padding(5.dp)
		.clip(RoundedCornerShape(5.dp))
		.background(Color(0xff495d92), shape = RoundedCornerShape(5.dp))
		.border(BorderStroke(3.dp, SolidColor(Color.Black))),) {
		Text(
			text = location.name,
			modifier = Modifier
				.padding(horizontal = 10.dp, vertical = 3.dp))
		Text(text = location.category.toString(),
			modifier = Modifier
				.padding(horizontal = 10.dp, vertical = 3.dp))
		Text(text = location.description,
			modifier = Modifier
				.padding(horizontal = 10.dp, vertical = 3.dp))
		}
	}

@Composable
fun LoadingScreen() {
	Box(
		modifier = Modifier
			.height(100.dp)
			.fillMaxWidth(),
        contentAlignment = Alignment.Center
	){
		/* Image( */
		/* 	painter = painterResource(id = R.drawable.ic_splash), // Image source */
		/* 	contentDescription = "Hidden Pearls Logo", */
		/* 	modifier = Modifier.fillMaxSize() */
		/* ) */
		CircularProgressIndicator(
			modifier = Modifier.width(64.dp),
			color = MaterialTheme.colorScheme.secondary,
			trackColor = MaterialTheme.colorScheme.surfaceVariant,
		)
	}
	/* Text(text = "Loading…") */
}

@Composable
fun ErrorScreen(error: String?) {
	if (error != null)
		Text(text = error)
	else
		Text(text = "Error!")
}
