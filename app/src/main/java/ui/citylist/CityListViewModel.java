package ui.citylist;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Model.currentWeather.WeatherData;
import Model.currentWeather.WeatherResponse;
import api.WeatherApiService;
import database.CityEntity;
import repository.CityRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CityListViewModel extends AndroidViewModel {
    private CityRepository cityRepository;
    private LiveData<List<CityEntity>> cityList;
    private MutableLiveData<WeatherData> weatherData;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CityListViewModel(@NonNull Application application) {
        super(application);
        cityRepository = new CityRepository(application);
        cityList = cityRepository.getAllCities();
        weatherData = new MutableLiveData<>();
    }

    public LiveData<List<CityEntity>> getCityList() {
        return cityList;
    }

    public MutableLiveData<WeatherData> getWeatherData() {
        return weatherData;
    }

    public void insert(CityEntity city) {
        executorService.execute(() -> cityRepository.insert(city));
    }

    public void loadCitiesFromJson() {
        AssetManager assetManager = getApplication().getAssets();
        try (InputStream is = assetManager.open("cities.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            JSONArray cityArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < cityArray.length(); i++) {
                JSONObject cityObject = cityArray.getJSONObject(i);
                String name = cityObject.getString("name");
                String country = cityObject.getString("country");
                double latitude = cityObject.getDouble("latitude");
                double longitude = cityObject.getDouble("longitude");
                CityEntity city = new CityEntity(name, country, latitude, longitude);

                insert(city);
            }
        } catch (IOException | JSONException e) {
            Log.e("CityListViewModel", "Error loading cities from JSON", e);
        }
    }

    public void fetchWeatherDataForCity(String cityName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.weatherbit.io/v2.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApiService weatherApi = retrofit.create(WeatherApiService.class);
        Call<WeatherResponse> call = weatherApi.getCurrentWeatherByCity(cityName, "fb0111d3c64c419989a6e5aaa834fdf6");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getData().isEmpty()) {
                    WeatherData weather = response.body().getData().get(0);
                    weatherData.setValue(weather);
                } else {
                    Log.e("CityListViewModel", "No weather data found");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("CityListViewModel", "Error fetching weather data", t);
            }
        });
    }
}
