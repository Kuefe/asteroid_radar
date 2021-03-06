package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.network.ImageOfTheDayApi
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AsteroidFilter { TODAY, WEEK, SAVED }
enum class AsteroidLoadStaus { LOADING, ERROR, DONE }


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

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<AsteroidLoadStaus>()

    // The external immutable LiveData for the request status
    val status: LiveData<AsteroidLoadStaus>
        get() = _status

    // The internal MutableLiveData AsteroidApiStatus that stores the most recent response status
    private val _asteroids = MutableLiveData<List<Asteroid>>()

    // The external immutable LiveData for the AsteroidApiStatus
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    /**
     * Livedata für the Image of the day
     */
    private val _imageOfTheDay = MutableLiveData<PictureOfDay>()

    val imageOfTheDay: LiveData<PictureOfDay>
        get() = _imageOfTheDay

    // Refresh the asteroids using the repository
    init {
        val filter = AsteroidFilter.SAVED
        _status.value = AsteroidLoadStaus.LOADING
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshAsteriods()
                _asteroids.value =
                    asteroidsRepository.getAsteroidList(getToday(), getEndDate(filter))
                _imageOfTheDay.value = ImageOfTheDayApi.retrofitService.getImageOfTheDay()
                _status.value = AsteroidLoadStaus.DONE
            } catch (e: java.lang.Exception) {
                _asteroids.value = ArrayList()
                _status.value = AsteroidLoadStaus.ERROR
            }
        }

    }

    // Display the database asterids
    fun updateFilter(filter: AsteroidFilter) {
        viewModelScope.launch {
            try {
                _asteroids.value =
                    asteroidsRepository.getAsteroidList(getToday(), getEndDate(filter))
            } catch (e: Exception) {
                _asteroids.value = ArrayList()
            }
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


    /**
     * return today as a string
     * calculate the startdate of the filter
     */
    fun getToday(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    /**
     * calculate the date for the filter
     * Week-Filter: Sunday until Saturday
     * saved-filter: from today the next seven days
     */
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
