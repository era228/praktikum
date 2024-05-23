package com.example.finances.api

import com.example.finances.model.CbrResult
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CbrService {

    @GET("/daily_json.js")
    suspend fun getDaily(): Response<CbrResult>

    companion object {
        fun getInstance(): CbrService {
            return Retrofit.Builder()
                .baseUrl("https://www.cbr-xml-daily.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CbrService::class.java)
        }
    }
}