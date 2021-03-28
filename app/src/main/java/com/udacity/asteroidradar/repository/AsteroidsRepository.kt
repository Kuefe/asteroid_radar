package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    /**
     * Transform the dataabase object to a domain object
     */
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    /**
     * Refresh the offline cache (database)
     */
    suspend fun refreshAsteriods() {
        withContext(Dispatchers.IO) {
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
            database.asteroidDao.insertAll(*networkAsteroidList.asDatabaseModel().toTypedArray())
        }
    }
}


