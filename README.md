# Hints to this project

Here are some hints about the project

## API-Key
The API key has been stored in the file. The API key "DEMO_KEY" has been set as default.
...
app/src/main/java/com/udacity/asteroidradar/Constants.kt
...
Please change the API key to your API key before running the app

## Requirements for the worker
The app downloads the next 7 days asteroids and saves them in the database once a day using workManager with requirements of 
internet connection and device plugged in
...
.setRequiredNetworkType(NetworkType.UNMETERED)
.setRequiresCharging(true)
...

## Filter
### View weeks asteroids
Show the asteroids from todasy until next Saturday

### View today asteroids
Show the asteroids from today

### View saved asteroids
Show the asteroids of the next 7 days

### image of the day
It will display R.drawable.placeholder_picture_of_day if the mediatype = video