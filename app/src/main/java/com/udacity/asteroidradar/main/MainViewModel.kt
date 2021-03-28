package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // The internal MutableLiveData AsteroidApiStatus that stores the most recent response status
    private val _status = MutableLiveData<AsteroidApiStatus>()

    // The external immutable LiveData for the AsteroidApiStatus
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    // Create the database singleton
    private val database = getDatabase(application)

    // Create repository
    private val asteroidsRepository = AsteroidsRepository(database)

    val asteriods = asteroidsRepository.asteroids

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid


    // Refresh the asteroids using the repository
    init {
        _status.value = AsteroidApiStatus.LOADING
        try {
            viewModelScope.launch { asteroidsRepository.refreshAsteriods() }
            _status.value = AsteroidApiStatus.DONE
        } catch (e: Exception) {
            _status.value = AsteroidApiStatus.ERROR
        }

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
}