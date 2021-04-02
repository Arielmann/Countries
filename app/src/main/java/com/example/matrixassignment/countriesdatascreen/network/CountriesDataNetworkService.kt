package com.example.matrixassignment.countriesdatascreen.network

import com.example.matrixassignment.countriesdatascreen.model.Country
import retrofit2.Call
import retrofit2.http.*


interface CountriesDataNetworkService {

    @GET("rest/v2/all")
    fun fetchCountriesRawData(): Call<List<Country>>

}
