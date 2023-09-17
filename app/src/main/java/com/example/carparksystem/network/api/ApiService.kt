package com.example.carparksystem.network.api

import com.example.carparksystem.network.model.response.CarparkApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/v1/transport/carpark-availability")
    suspend fun getCarparkAvailability(
        @Query("date_time") date_time: String
    ): Response<CarparkApiResponse.CarparkAvailability>
}