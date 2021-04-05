package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AsteroidFilter { TODAY, WEEK, SAVED }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // Create the database singleton
    private val database = getDatabase(application)

    // Create repository
    private val asteroidsRepository = AsteroidsRepository(database)

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    // The internal MutableLiveData AsteroidApiStatus that stores the most recent response status
    private val _asteroids = MutableLiveData<List<Asteroid>>()

    // The external immutable LiveData for the AsteroidApiStatus
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids


    fun updateFilter(filter: AsteroidFilter) {
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshAsteriods()
                _asteroids.value =
                    asteroidsRepository.getAsteroidList(getToday(), getEndDate(filter))
            } catch (e: Exception) {
                _asteroids.value = ArrayList()
            }
        }
    }

    // Refresh the asteroids using the repository
    init {
        updateFilter(AsteroidFilter.SAVED)
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param marsProperty The [MarsProperty] that was clicked on.
     */
    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedAsteroid is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }


    fun getToday(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun getEndDate(filter: AsteroidFilter): String {
        val calendar = Calendar.getInstance()
        if (filter == AsteroidFilter.SAVED) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }
        if (filter == AsteroidFilter.WEEK) {
            calendar.add(Calendar.DAY_OF_YEAR, 7 - calendar.get(Calendar.DAY_OF_WEEK))
        }
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}