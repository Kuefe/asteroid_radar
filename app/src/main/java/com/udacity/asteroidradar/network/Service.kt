package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


/**
 * Use the Retrofit builder to build a retrofit object
 * object pointing to the desired URL
 */
private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

/**
 * A public interface that exposes the [getAsteroids] method
 */
interface AsteroidApiService {
    @GET("neo/rest/v1/feed?api_key=" + API_KEY)
    suspend fun getAsteroids(): String
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}