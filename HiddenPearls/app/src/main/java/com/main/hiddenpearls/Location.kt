package com.main.hiddenpearls;

import java.time.YearMonth;
import java.time.Month
import android.location.Location as GPSLocation;
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encodeToString

enum class LocationCategory {
	PEARL, TRAP
}

@Serializable
@SerialName("YearMonth")
private class YearMonthSurrogate(val year: Int, val month: Month) {}

object YearMonthSerializer : KSerializer<YearMonth> {
	override val descriptor: SerialDescriptor = YearMonthSurrogate.serializer().descriptor
	override fun serialize(encoder: Encoder, value: YearMonth) {
		val surrogate = YearMonthSurrogate(value.getYear(), value.getMonth())
		encoder.encodeSerializableValue(YearMonthSurrogate.serializer(), surrogate)
	}

	override fun deserialize(decoder: Decoder): YearMonth {
		val surrogate = decoder.decodeSerializableValue(YearMonthSurrogate.serializer())
		return YearMonth.of(surrogate.year, surrogate.month)
	}
}

@Serializable
@SerialName("GPSLocation")
private class GPSLocationSurrogate(
	val longitude: Double,
	val latitude: Double
) {}

object GPSLocationSerializer : KSerializer<GPSLocation> {
	override val descriptor: SerialDescriptor = GPSLocationSurrogate.serializer().descriptor

	override fun serialize(encoder: Encoder, value: GPSLocation) {
		val surrogate = GPSLocationSurrogate(
			longitude = value.getLongitude(),
			latitude = value.getLatitude()
		)
		encoder.encodeSerializableValue(GPSLocationSurrogate.serializer(), surrogate)
	}

	override fun deserialize(decoder: Decoder): GPSLocation {
		val surrogate = decoder.decodeSerializableValue(GPSLocationSurrogate.serializer())
		val location = GPSLocation("")
		location.setLongitude(surrogate.longitude)
		location.setLatitude(surrogate.latitude)
		return location
	}
}

@Serializable
data class VisitStatistic (
	@Serializable(with=YearMonthSerializer::class)
	val time: YearMonth,
	val visitors: Int
){}

@Serializable
data class Location (
	val id: Long,
	val name: String,
	val description: String,
	val category: LocationCategory,
	@Serializable(with=GPSLocationSerializer::class)
	val location: GPSLocation,
	val statistics: List<VisitStatistic>
) {}

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
