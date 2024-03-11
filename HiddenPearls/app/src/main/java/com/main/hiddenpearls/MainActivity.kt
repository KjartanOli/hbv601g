package com.main.hiddenpearls

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.main.hiddenpearls.ui.theme.HiddenPearlsTheme


object PreferencesSerializer {
	val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
		name = "favorites",
	)
}

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val dataStore = applicationContext.dataStore
		FavoritesService.init(this)
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
