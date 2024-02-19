package com.main.hiddenpearls.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.main.hiddenpearls.Location
import com.main.hiddenpearls.LocationService
import com.main.hiddenpearls.ShakeForPearl

@Composable
fun HomeView(
	pearls: List<Location>,
	traps: List<Location>,
	onNavigateToList: () -> Unit,
	onNavigateToDetails: (id: Long) -> Unit,
	modifier: Modifier = Modifier,
	navController: NavHostController,
	locations: LocationService
) {
	ShakeForPearl(navController, locations)

	Column (
		modifier = Modifier
			.padding(12.dp)
	) {
		LocationList(
			heading = "Pearls",
			locations = pearls,
			onNavigateToDetails = onNavigateToDetails
		)
		LocationList(
			heading = "Traps",
			locations = traps,
			onNavigateToDetails = onNavigateToDetails
		)
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
	}
}

@Composable
fun LocationDetails(location: Location) {
	Column (modifier = Modifier
		.padding(12.dp)
	) {
		Text(text = location.name)
		Text(text = location.category.toString())
		Text(text = location.description)
	}
}
