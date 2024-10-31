package ui.home;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.util.List;

import Model.currentWeather.Weather;
import Model.dailyWeather.DailyWeather;
import Model.hourlyWeather.HourlyWeather;
import repository.WeatherRepository;
import utils.LocationManager;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = "HomeViewModel";
    private LocationManager locationManager;
    private final WeatherRepository weatherRepository;
    private final MutableLiveData<Weather> currentWeatherData;
    private final MutableLiveData<List<HourlyWeather>> hourlyWeatherData;
    private final MutableLiveData<List<DailyWeather>> dailyWeatherData;

    public HomeViewModel(@NonNull Application application, LocationManager locationManager) {
        super(application);
        Log.d(TAG, "HomeViewModel initialized");
        this.locationManager = locationManager;
        currentWeatherData = new MutableLiveData<>();
        hourlyWeatherData = new MutableLiveData<>();
        dailyWeatherData = new MutableLiveData<>();
        weatherRepository = new WeatherRepository(application.getApplicationContext());
        initializeWeatherData();
    }

    private void initializeWeatherData() {
        Log.d(TAG, "Initializing weather data");

        // Cập nhật vị trí trước khi lấy dữ liệu thời tiết
        locationManager.updateCurrentLocation(location -> {
            if (location != null) {
                Log.d(TAG, String.format("Location updated - Lat: %f, Long: %f",
                        location.getLatitude(),
                        location.getLongitude()));
                loadCurrentWeatherData();
                try {
                    loadHourlyWeatherData();
                    loadDailyWeatherData();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.e(TAG, "Location is null after update");
            }
        });
    }

    private void loadCurrentWeatherData() {
        Double latitude = locationManager.getLatitude();
        Double longitude = locationManager.getLongitude();

        if (latitude == null || longitude == null) {
            Log.e(TAG, "Location coordinates are null");
            return;
        }

        LiveData<Weather> repoData = weatherRepository.getCurrentWeather(null, latitude, longitude);

        repoData.observeForever(weatherResponse -> {
            Log.d(TAG, "Received weather update: " +
                    (weatherResponse != null ? weatherResponse.toString() : "null"));
            currentWeatherData.setValue(weatherResponse);
        });
    }

    private void loadHourlyWeatherData() throws ParseException {
        Double latitude = locationManager.getLatitude();
        Double longitude = locationManager.getLongitude();

        LiveData<List<HourlyWeather>> repoData = weatherRepository.getHourlyWeather(null, latitude, longitude);

        repoData.observeForever(hourlyWeatherList -> {
            if (hourlyWeatherList == null || hourlyWeatherList.isEmpty()) {
                hourlyWeatherData.setValue(null);
                return;
            }
            hourlyWeatherData.setValue(hourlyWeatherList);
        });

    }

    private void loadDailyWeatherData() throws ParseException {
        Double latitude = locationManager.getLatitude();
        Double longitude = locationManager.getLongitude();

        LiveData<List<DailyWeather>> repoData = weatherRepository.getDailyWeather(null, latitude, longitude);

        repoData.observeForever(dailyWeatherList -> {
            if (dailyWeatherList == null || dailyWeatherList.isEmpty()) {
                dailyWeatherData.setValue(null);
                return;
            }
            for (DailyWeather dailyWeather : dailyWeatherList) {
                Log.d(TAG, "Received daily weather update: " + dailyWeather.getDatetime());
                Log.d(TAG, "Received daily weather update: " + dailyWeather.getTemp());
            }
            dailyWeatherData.setValue(dailyWeatherList);
        });
    }

    public LiveData<Weather> getCurrentWeatherData() {
        return currentWeatherData;
    }

    public LiveData<List<HourlyWeather>> getHourlyWeatherData() {
        return hourlyWeatherData;
    }
}

