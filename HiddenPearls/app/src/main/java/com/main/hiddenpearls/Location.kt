package com.main.hiddenpearls;

import java.time.YearMonth;
import android.location.Location as GPSLocation;

enum class LocationCategory {
	PEARL, TRAP
}

class VisitStatistic (val time: YearMonth, val visitors: Int){
}

class Location (
	val id: Long,
	val name: String,
	val description: String,
	val category: LocationCategory,
	val location: GPSLocation,
	val statistics: List<VisitStatistic>
) {

}

class LocationService() {
	fun getLocations(limit: Int? = null): List<Location> {
		return listOf<Location>();
	}

	fun searchById(id: Long): Location {
		return Location(
			id = id,
			name = "Test",
			description = "lorem ipsum dolor",
			category = LocationCategory.PEARL,
			location = GPSLocation(""),
			statistics = listOf<VisitStatistic>()
		)
	}

	fun searchByCategory(category: LocationCategory, limit: Int? = null): List<Location> {
		return if (category == LocationCategory.PEARL)
				   {
					   listOf<Location>(Location(
						   id = 0,
						   name = "Test",
						   description = "lorem ipsum dolor",
						   category = LocationCategory.PEARL,
						   location = GPSLocation(""),
						   statistics = listOf<VisitStatistic>()
					   ))
				   }
				   else
				   {
					   listOf<Location>(Location(
						   id = 1,
						   name = "Test",
						   description = "set amet",
						   category = LocationCategory.TRAP,
						   location = GPSLocation(""),
						   statistics = listOf<VisitStatistic>()
					   ))
				   }
	}
}
