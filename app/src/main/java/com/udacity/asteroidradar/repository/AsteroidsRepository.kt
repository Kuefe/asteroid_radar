package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    /**
     * Refresh the offline cache (database)
     * Network --> database
     */
    suspend fun refreshAsteriods() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidList =
                    parseAsteroidsJsonResult(JSONObject(AsteroidApi.retrofitService.getAsteroids()))
                val networkAsteroidList = asteroidList.map {
                    NetworkAsteroid(
                        it.id,
                        it.codename,
                        it.closeApproachDate,
                        it.absoluteMagnitude,
                        it.estimatedDiameter,
                        it.relativeVelocity,
                        it.distanceFromEarth,
                        it.isPotentiallyHazardous
                    )
                }
                database.asteroidDao.insertAll(
                    *networkAsteroidList.asDatabaseModel().toTypedArray()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getAsteroidList(startDate: String, endDate: String): List<Asteroid> {
        return withContext(Dispatchers.IO) {
            val asteroidList: List<DatabaseAsteroid> =
                database.asteroidDao.getAsteroids(startDate, endDate)
            asteroidList.asDomainModel()
        }
    }
}


