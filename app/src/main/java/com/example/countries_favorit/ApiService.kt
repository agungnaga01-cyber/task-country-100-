package com.example.countries_favorit

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("flag/images")
    fun getCountries(): Call<ApiResponse>
}