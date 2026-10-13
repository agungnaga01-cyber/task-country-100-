package com.example.countries_favorit

data class Country(
    val name: String,
    val flag: String,
    val iso2: String,
    val iso3: String
)

data class ApiResponse(
    val error: Boolean,
    val msg: String,
    val data: List<Country>
)