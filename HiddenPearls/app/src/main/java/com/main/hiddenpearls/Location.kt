package com.main.hiddenpearls

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Month
import java.time.YearMonth
import android.location.Location as GPSLocation

enum class LocationCategory {
	PEARL, TRAP
}

@Serializable
@SerialName("YearMonth")
private class YearMonthSurrogate(val year: Int, val month: Month)

object YearMonthSerializer : KSerializer<YearMonth> {
	override val descriptor: SerialDescriptor = YearMonthSurrogate.serializer().descriptor
	override fun serialize(encoder: Encoder, value: YearMonth) {
		val surrogate = YearMonthSurrogate(value.year, value.month)
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
)

object GPSLocationSerializer : KSerializer<GPSLocation> {
	override val descriptor: SerialDescriptor = GPSLocationSurrogate.serializer().descriptor

	override fun serialize(encoder: Encoder, value: GPSLocation) {
		val surrogate = GPSLocationSurrogate(
			longitude = value.longitude,
			latitude = value.latitude
		)
		encoder.encodeSerializableValue(GPSLocationSurrogate.serializer(), surrogate)
	}

	override fun deserialize(decoder: Decoder): GPSLocation {
		val surrogate = decoder.decodeSerializableValue(GPSLocationSurrogate.serializer())
		val location = GPSLocation("")
		location.longitude = surrogate.longitude
		location.latitude = surrogate.latitude
		return location
	}
}

@Serializable
data class VisitStatistic (
	@Serializable(with=YearMonthSerializer::class)
	val time: YearMonth,
	val visitors: Int
)

@Serializable
data class Location (
	val id: Long,
	val name: String,
	val description: String,
	val category: LocationCategory,
	@Serializable(with=GPSLocationSerializer::class)
	@SerialName("loc")
	val location: GPSLocation,
	val statistics: List<VisitStatistic>
)
