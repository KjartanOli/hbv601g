package com.main.hiddenpearls

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.main.hiddenpearls.ui.DetailsView
import com.main.hiddenpearls.ui.FavoritesView
import com.main.hiddenpearls.ui.GPSSearchView
import com.main.hiddenpearls.ui.HomeView
import com.main.hiddenpearls.ui.ListView
import com.main.hiddenpearls.ui.NameSearchView
import kotlin.math.pow
import kotlin.math.sqrt

sealed class Screen(val route: String) {
	data object Home : Screen("home")
	data object Favorites : Screen("favorites")
	data object LocationList : Screen("location_list")
	data object Details : Screen("location")
	data object Random : Screen("random")
	data object NameSearch : Screen("search/name")
	data object GPSSearch : Screen("search/gps")
}

@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController,
	startDestination: String = "home"
) {
	val onNavigateToDetails = {
		id: Long -> navController.navigate("${Screen.Details.route}/$id")
	}

	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = startDestination
	) {

		composable(Screen.Home.route) {
			Scaffold(bottomBar = { NavBar(navController) }) { innerPadding ->
				HomeView(
					onNavigateToDetails = onNavigateToDetails,
					navController = navController,
					modifier = Modifier.padding(innerPadding)
				)
			}

		}

		composable(Screen.LocationList.route) {
			Scaffold(bottomBar = { NavBar(navController) }) { innerPadding ->
				ListView(
					onNavigateToDetails = onNavigateToDetails,
					modifier = Modifier.padding(innerPadding)
				)
			}
		}

		composable(Screen.Favorites.route) {
			Scaffold(bottomBar = { NavBar(navController) }) { innerPadding ->
				FavoritesView(
					modifier = modifier.padding(innerPadding),
					onNavigateToDetails = onNavigateToDetails
				)
			}
		}

		composable(
			"${Screen.Details.route}/{id}",
			arguments = listOf(navArgument("id") { type = NavType.LongType })
		) { backStackEntry ->
			val id = backStackEntry.arguments?.getLong("id")

			if (id != null) {
				Scaffold(bottomBar = { NavBar(navController) }) { innerPadding ->
					DetailsView(modifier = modifier.padding(innerPadding))
				}
			}
		}

		composable(
			"${Screen.NameSearch.route}/{searchQuery}",
			arguments = listOf(navArgument("searchQuery") { type = NavType.StringType })
		) { backStackEntry ->
			val searchQuery = backStackEntry.arguments?.getString("searchQuery")

			if (searchQuery != null) {
				val searchResults = remember { mutableStateOf<List<Location>>(listOf()) }
				LaunchedEffect(searchQuery) {
					searchResults.value = LocationService.searchByName(searchQuery)
				}

				Scaffold(bottomBar = { NavBar(navController) }) { innerPadding ->
					NameSearchView(
						modifier = modifier.padding(innerPadding),
						onNavigateToDetails = onNavigateToDetails
					)
				}
			}
		}

		composable("${Screen.GPSSearch.route}/{radius}",
			arguments = listOf(navArgument("radius") { type = NavType.FloatType })
		) {
			backStackEntry ->
			val radius = backStackEntry.arguments?.getFloat("radius")?.toDouble() ?: 0.0
			Scaffold(bottomBar = { NavBar(navController) }) { innerPadding ->
				GPSSearchView(
					modifier = modifier.padding(innerPadding),
					onNavigateToDetails = onNavigateToDetails,
					radius = radius
				)
			}
		}
	}
}

@Composable
fun NavBar(navController: NavHostController) {
	val showNameSearchDialog = remember { mutableStateOf(false) }
	val showGPSSearchDialog = remember { mutableStateOf(false) }

	BottomAppBar(
		actions = {
			Row(
				modifier = Modifier
					.fillMaxHeight()
					.fillMaxWidth()
					.padding(8.dp),
				horizontalArrangement = Arrangement.Center
			) {
				// Name Search
				IconButton(onClick = { showNameSearchDialog.value = true },
					modifier = Modifier
						.fillMaxSize()
						.weight(1f)) {
					Icon(Icons.Filled.Search,
						contentDescription = "Search",
						modifier = Modifier
							.fillMaxSize()
							.padding(5.dp)
					)
				}
				// Geo Search
				IconButton(onClick = { showGPSSearchDialog.value = true },
					modifier = Modifier
						.fillMaxSize()
						.weight(1f)) {
					Icon(Icons.Filled.LocationOn,
						contentDescription = "Nearby",
						modifier = Modifier
							.fillMaxSize()
							.padding(5.dp)
					)
				}
				// Home
				IconButton(onClick = { navController.navigate(Screen.Home.route) },
					modifier = Modifier
						.fillMaxSize()
						.weight(1f)) {
					Icon(
						Icons.Filled.Home,
						contentDescription = "Home",
						modifier = Modifier
							.fillMaxSize()
							.padding(3.dp)
					)
				}
				// Favorites
				IconButton(onClick = { navController.navigate(Screen.Favorites.route) },
					modifier = Modifier
						.fillMaxSize()
						.weight(1f)) {
					Icon(
						Icons.Filled.Favorite,
						contentDescription = "Favorites",
						modifier = Modifier
							.fillMaxSize()
							.padding(5.dp)
					)
				}
				// Location List
				IconButton(onClick = { navController.navigate(Screen.LocationList.route) },
					modifier = Modifier
						.fillMaxSize()
						.weight(1f)) {
					Icon(
						Icons.AutoMirrored.Filled.List,
						contentDescription = "All Locations",
						modifier = Modifier
							.fillMaxSize()
							.padding(5.dp)
					)
				}
			}
		}
	)

	// Dialog for NameSearch
	if (showNameSearchDialog.value) {
		// variable for search entry
		var searchQuery by remember { mutableStateOf("") }

		AlertDialog(
			onDismissRequest = { showNameSearchDialog.value = false },
			title = { Text("Search") },
			text = {
					TextField(
						value = searchQuery,
						onValueChange = { searchQuery = it },
						label = { Text("Search for a pearl or trap...") }
					)
			},
			dismissButton = {
				Button(onClick = { showNameSearchDialog.value = false }) {
					Text("Cancel")
				}
							},
			confirmButton = {
				Button(onClick = {
					showNameSearchDialog.value = false
					// if no text is entered, dialog is closed on pressing search
					if ( searchQuery.isNotEmpty()) {
						// Pass the search query to the route
						navController.navigate("${Screen.NameSearch.route}/$searchQuery")
					}

				}) {
					Text("Search")
				}
			}
		)
	}
	// Dialog for GPSSearch
	if (showGPSSearchDialog.value) {
		var radius by remember { mutableFloatStateOf(0f) }

		AlertDialog(
			onDismissRequest = { showGPSSearchDialog.value = false },
			title = { Text("Radius Search") },
			text = {
				// State for the text field

				Column {
					Text(text = "Search radius: ${radius.toInt()} km",
						modifier = Modifier
						.padding(5.dp))
					Slider(
						value = radius,
						onValueChange = { radius = it },
						valueRange = 0f..100f
					)
				}
			},
			dismissButton = {
				Button(onClick = { showGPSSearchDialog.value = false }) {
					Text("Cancel")
				}
			},
			confirmButton = {
				Button(onClick = {
					showGPSSearchDialog.value = false
					navController.navigate("${Screen.GPSSearch.route}/$radius")
					// use search results here, stored in 'dist'
				}) {
					Text("Search")
				}
			}
		)
	}
}

// Gives you a random pearl when you shake the phone on the home screen of the app
@Composable
fun ShakeForPearl(navController: NavHostController) {
	val context = LocalContext.current
	val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
	val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

	val sensorEventListener = remember {
		object : SensorEventListener {
			override fun onSensorChanged(event: SensorEvent?) {
				val gyroChange = sqrt(
					event?.values?.get(0)?.pow(2) ?: (0f
						+ (event?.values?.get(1)?.pow(2) ?: 0f)
						+ (event?.values?.get(2)?.pow(2) ?: 0f))
				)

				val threshold = 6.0f
				// Get a random location
				// might offload picking random location to api
				if (gyroChange > threshold) {
					navController.navigate(Screen.Random.route)
				}

			}

			override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
				// Handle changes in sensor accuracy if needed
			}
		}
	}

	DisposableEffect(Unit) {
		sensorManager.registerListener(sensorEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)

		// Unregister the listener when the composable is disposed
		onDispose {
			sensorManager.unregisterListener(sensorEventListener)
		}
	}
}

// previews below
@Preview
@Composable
fun NavBarPreview() {
	NavBar(rememberNavController())
}
