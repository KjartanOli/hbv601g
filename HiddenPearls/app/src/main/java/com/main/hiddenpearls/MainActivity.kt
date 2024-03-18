package com.main.hiddenpearls

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.main.hiddenpearls.ui.theme.HiddenPearlsTheme

class MainActivity : ComponentActivity() {

	private lateinit var fusedLocationClient: FusedLocationProviderClient
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		FavoritesService.init(this)
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		setContent {
			HiddenPearlsTheme {
				// A surface container using the 'background' color from the theme
				Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
					AppNavHost(navController = rememberNavController())
				}
			}
		}
	}
}
