package com.main.hiddenpearls

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavArgument
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier

import com.main.hiddenpearls.ui.HomeView
import com.main.hiddenpearls.ui.LocationList
import com.main.hiddenpearls.ui.LocationCard
import com.main.hiddenpearls.ui.LocationDetails

enum class Screen {
    HOME, LOCATION_LIST, LOCATION_DETAILS
}

@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController,
	startDestination: String = Screen.HOME.name
) {
	val locations = LocationService()

	val onNavigateToDetails = {
		id: Long ->  navController.navigate("${Screen.LOCATION_DETAILS.name}/$id")
	}

	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = startDestination
	) {
		composable(Screen.HOME.name) {
			val pearls = locations.searchByCategory(LocationCategory.PEARL, 5);
			val traps = locations.searchByCategory(LocationCategory.TRAP, 5);
			HomeView(
				pearls = pearls,
				traps = traps,
				onNavigateToList = {
					navController.navigate(Screen.LOCATION_LIST.name)
				},
				onNavigateToDetails = onNavigateToDetails
			)
		}

		composable(Screen.LOCATION_LIST.name) {
			LocationList(heading = "Locations", locations=locations.getLocations(), onNavigateToDetails = onNavigateToDetails)
		}

		composable(
			"${Screen.LOCATION_DETAILS.name}/{id}",
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
