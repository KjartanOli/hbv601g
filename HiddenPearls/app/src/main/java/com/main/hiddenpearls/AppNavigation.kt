package com.main.hiddenpearls

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavArgument
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import com.main.hiddenpearls.ui.HomeView
import com.main.hiddenpearls.ui.LocationList
import com.main.hiddenpearls.ui.LocationCard
import com.main.hiddenpearls.ui.LocationDetails

/* enum class Screen { */
/*     HOME, LOCATION_LIST, LOCATION_DETAILS */
/* } */

sealed class Screen(val route: String) {
	object Home : Screen("home")
	object LocationList : Screen("location_list")
	object LocationDetails : Screen("location")
	object NameSearch : Screen("search/name")
	object GPSSearch : Screen("search/gps")
}

@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController,
	startDestination: String = Screen.Home.route
) {
	val locations = LocationService()

	val onNavigateToDetails = {
		id: Long ->  navController.navigate("${Screen.LocationDetails.route}/$id")
	}

	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = startDestination
	) {
		composable(Screen.Home.route) {
			val pearls = locations.searchByCategory(LocationCategory.PEARL, 5);
			val traps = locations.searchByCategory(LocationCategory.TRAP, 5);
			Scaffold(bottomBar = { NavBar(navController) }) { innerPadding ->
				HomeView(
					pearls = pearls,
					traps = traps,
					modifier = modifier.padding(innerPadding),
					onNavigateToList = {
						navController.navigate(Screen.LocationList.route)
					},
					onNavigateToDetails = onNavigateToDetails
				)
			}
		}

		composable(Screen.LocationList.route) {
			Scaffold(bottomBar = { NavBar(navController) }) { innerPadding ->
				LocationList(
					heading = "Locations",
					locations=locations.getLocations(),
					modifier = modifier.padding(innerPadding),
					onNavigateToDetails = onNavigateToDetails
				)
			}
		}

		composable(
			"${Screen.LocationDetails.route}/{id}",
			arguments = listOf(navArgument("id") { type = NavType.LongType })
		) { backStackEntry ->
			val id = backStackEntry.arguments?.getLong("id")

			if (id != null) {
				val location = locations.searchById(id)
				LocationDetails(location = location)
			}
		}
	}
}

@Composable
fun NavBar(navController: NavHostController) {
	val showDialog = remember { mutableStateOf(false) }

	BottomAppBar(
		actions = {
			IconButton(onClick = {navController.navigate(Screen.Home.route)}) {
				Icon(Icons.Filled.Home, contentDescription = "Home")
			}
			IconButton(onClick = {showDialog.value = true}) {
				Icon(Icons.Filled.Search, contentDescription = "Search")
			}
			IconButton(onClick = {navController.navigate(Screen.GPSSearch.route)}) {
				Icon(Icons.Filled.LocationOn, contentDescription = "Nearby")
			}
			IconButton(onClick = {navController.navigate(Screen.LocationList.route)}) {
				Icon(Icons.Filled.List, contentDescription = "All Locations")
			}
		}
	)

	//


	if (showDialog.value) {
		AlertDialog(
			onDismissRequest = { showDialog.value = false },
			title = { Text("Search") },
			text = {
					// State for the text field
					var text by remember { mutableStateOf("") }

					TextField(
						value = text,
						onValueChange = { text = it },
						label = { Text("Search for a pearl or trap...") }
					)
				   },
			dismissButton = {
				Button(onClick = { showDialog.value = false }) {
					Text("Cancel")
				}
							},
			confirmButton = {
				Button(onClick = { showDialog.value = false
				// use search results here, in 'text'
					}) {
					Text("Search")
				}
			}
		)
	}
}
