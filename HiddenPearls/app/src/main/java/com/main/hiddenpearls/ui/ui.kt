package com.main.hiddenpearls.ui

import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column

import com.main.hiddenpearls.Location

@Composable
fun HomeView(
	pearls: List<Location>,
	traps: List<Location>,
	onNavigateToList: () -> Unit,
	onNavigateToDetails: (id: Long) -> Unit
) {
	Column {
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
	onNavigateToDetails: (id: Long) -> Unit
) {
	Column {
		Text(text = heading)
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
	Column {
		Text(text = location.name)
		Text(text = location.category.toString())
		Text(text = location.description)
	}
}
