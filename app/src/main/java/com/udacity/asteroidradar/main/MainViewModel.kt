package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.getDatabase
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
}