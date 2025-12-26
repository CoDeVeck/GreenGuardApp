package com.example.greenguard.domain.model.dto

data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val formatted_address: String,
    val address_components: List<AddressComponent>,
    val geometry: Geometry
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)