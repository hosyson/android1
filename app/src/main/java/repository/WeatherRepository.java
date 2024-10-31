package repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import Model.currentWeather.Weather;
import Model.currentWeather.WeatherData;
import Model.currentWeather.WeatherResponse;
import Model.dailyWeather.DailyWeather;
import Model.dailyWeather.DailyWeatherData;
import Model.dailyWeather.DailyWeatherResponse;
import Model.hourlyWeather.HourlyWeather;
import Model.hourlyWeather.HourlyWeatherData;
import Model.hourlyWeather.HourlyWeatherResponse;
import api.WeatherApiService;
import database.WeatherDao;
import database.WeatherDatabase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import utils.LocationManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class WeatherRepository {
    private static final String BASE_URL = "https://api.weatherbit.io/v2.0/";
    private static final String API_KEY = "fb0111d3c64c419989a6e5aaa834fdf6";
    private static final String TAG = "WeatherRepository";
    private final WeatherApiService apiService;
    private final WeatherDao weatherDao;
    private final LocationManager locationManager;
    private final Context context;

    public WeatherRepository(Context context) {
        this.context = context;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WeatherApiService.class);
        WeatherDatabase db = WeatherDatabase.getInstance(context);
        weatherDao = db.weatherDao();
        locationManager = new LocationManager(context);
    }

    //lấy dữ liệu thời tiết tại thời điểm hiện tại
    public LiveData<Weather> getCurrentWeather(String city, Double latitude, Double longitude) {
        MutableLiveData<Weather> weatherData = new MutableLiveData<>();

        Call<WeatherResponse> call;
        if (city != null) {
            call = apiService.getCurrentWeatherByCity(city, API_KEY);
        } else {
            call = apiService.getCurrentWeatherByLocation(latitude, longitude, API_KEY);
        }

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse.getData() != null && !weatherResponse.getData().isEmpty()) {
                        Weather weather = convertResponseToWeather(weatherResponse.getData().get(0));
                        weatherData.setValue(weather);

                    } else {
                        weatherData.setValue(null);
                    }
                } else {
                    try {
                        Log.e(TAG, "Error Response: " +
                                (response.errorBody() != null ? response.errorBody().string() : "null"));
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    weatherData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                weatherData.setValue(null);
            }
        });

        return weatherData;
    }

    //lấy dữ liệu thời tiết trong 24h tính từ 0h đến 23h ngày hôm nay
    public LiveData<List<HourlyWeather>> getHourlyWeather(String city, Double latitude, Double longitude) throws ParseException {
        MutableLiveData<List<HourlyWeather>> hourlyWeatherData = new MutableLiveData<>();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int currentHour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        String nextDate = getNextDate(currentDate);

        Call<HourlyWeatherResponse> historyCall = (city != null)
                ? apiService.getHourlyHistoryByCity(city, currentDate, nextDate, API_KEY)
                : apiService.getHourlyHistoryByLocation(latitude, longitude, currentDate, nextDate, API_KEY);

        Call<HourlyWeatherResponse> forecastCall = (city != null)
                ? apiService.getHourlyForecastByCity(city, currentDate, nextDate, API_KEY)
                : apiService.getHourlyForecastByLocation(latitude, longitude, currentDate, nextDate, API_KEY);

        AtomicReference<List<HourlyWeather>> historicalWeathers = new AtomicReference<>(new ArrayList<>());
        AtomicReference<List<HourlyWeather>> forecastWeathers = new AtomicReference<>(new ArrayList<>());
        AtomicInteger completedCalls = new AtomicInteger(0);

        Runnable combineAndDeliverData = () -> {
            if (completedCalls.get() == 2) {
                try {
                    List<HourlyWeather> combinedList = new ArrayList<>();

                    List<HourlyWeather> historical = historicalWeathers.get();
                    List<HourlyWeather> forecast = forecastWeathers.get();

                    if (historical != null) {
                        historical.removeIf(weather -> Integer.parseInt(weather.getDatetime().split(":")[1]) > currentHour);
                        combinedList.addAll(historical);
                    }

                    if (forecast != null) {
                        forecast.removeIf(weather -> Integer.parseInt(weather.getDatetime().split(":")[1]) <= currentHour);
                        combinedList.addAll(forecast);
                    }

                    if (combinedList.isEmpty()) {
                        hourlyWeatherData.postValue(new ArrayList<>());
                        return;
                    }

                    Collections.sort(combinedList, Comparator.comparing(HourlyWeather::getDatetime));

                    List<HourlyWeather> todayData = combinedList.stream()
                            .filter(weather -> {
                                String weatherDate = weather.getDatetime().split(":")[0];
                                return weatherDate.equals(currentDate);
                            })
                            .collect(Collectors.toList());

                    hourlyWeatherData.postValue(todayData);
                } catch (Exception e) {
                    Log.e(TAG, "Error processing weather data", e);
                    hourlyWeatherData.postValue(new ArrayList<>());
                }
            }
        };

        // History call
        historyCall.enqueue(new Callback<HourlyWeatherResponse>() {
            @Override
            public void onResponse(Call<HourlyWeatherResponse> call, Response<HourlyWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HourlyWeatherResponse hourlyWeatherResponse = response.body();
                    if (hourlyWeatherResponse.getData() != null && !hourlyWeatherResponse.getData().isEmpty()) {
                        List<HourlyWeather> weatherList = hourlyWeatherResponse.getData().stream()
                                .map(data -> convertResponseToHourlyWeather(data))
                                .collect(Collectors.toList());
                        historicalWeathers.set(weatherList);
                    }
                } else {
                    try {
                        Log.e(TAG, "Historical weather API error: " +
                                (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                completedCalls.incrementAndGet();
                combineAndDeliverData.run();
            }

            @Override
            public void onFailure(Call<HourlyWeatherResponse> call, Throwable t) {
                Log.e(TAG, "Historical weather API call failed", t);
                completedCalls.incrementAndGet();
                combineAndDeliverData.run();
            }
        });

        // Forecast call
        forecastCall.enqueue(new Callback<HourlyWeatherResponse>() {
            @Override
            public void onResponse(Call<HourlyWeatherResponse> call, Response<HourlyWeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HourlyWeatherResponse weatherResponse = response.body();
                    if (weatherResponse.getData() != null && !weatherResponse.getData().isEmpty()) {
                        List<HourlyWeather> weatherList = weatherResponse.getData().stream()
                                .map(data -> convertResponseToHourlyWeather(data))
                                .collect(Collectors.toList());
                        forecastWeathers.set(weatherList);
                    }
                } else {
                    try {
                        Log.e(TAG, "Forecast weather API error: " +
                                (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                completedCalls.incrementAndGet();
                combineAndDeliverData.run();
            }

            @Override
            public void onFailure(Call<HourlyWeatherResponse> call, Throwable t) {
                Log.e(TAG, "Forecast weather API call failed", t);
                completedCalls.incrementAndGet();
                combineAndDeliverData.run();
            }
        });

        return hourlyWeatherData;
    }

    public LiveData<List<DailyWeather>> getDailyWeather(String city, Double latitude, Double longitude) throws ParseException {
        MutableLiveData<List<DailyWeather>> dailyWeatherData = new MutableLiveData<>();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String startDate = getThreeDaysBefore(currentDate);
        Log.d(TAG, "start date: " + startDate);

        Call<DailyWeatherResponse> historyCall = (city != null)
                ? apiService.getHistoryDailyByCity(city, startDate, currentDate, API_KEY)
                : apiService.getHistoryDailyByLocation(latitude, longitude, startDate, currentDate, API_KEY);

        Call<DailyWeatherResponse> forecastCall = (city != null)
                ? apiService.getDailyForecastByCity(city, 4, API_KEY)
                : apiService.getDailyForecastByLocation(latitude, longitude, 4, API_KEY);

        AtomicReference<List<DailyWeather>> historicalWeathers = new AtomicReference<>(new ArrayList<>());
        AtomicReference<List<DailyWeather>> forecastWeathers = new AtomicReference<>(new ArrayList<>());
        AtomicInteger completedCalls = new AtomicInteger(0);

        Runnable combineAndDeliverData = () -> {
            if (completedCalls.get() == 2) {
                try {
                    List<DailyWeather> combinedList = new ArrayList<>();

                    List<DailyWeather> historical = historicalWeathers.get();
                    if (historical != null && !historical.isEmpty()) {
                        historical.removeIf(weather -> weather.getDatetime().equals(currentDate));
                        Log.d("DEBUG", "" + currentDate);
                        Log.d(TAG, "Historical weather data retrieved: " + historical.size() + " entries");
                        historical.forEach(weather ->
                                Log.d(TAG, "Historical entry date: " + weather.getDatetime())
                        );
                        combinedList.addAll(historical);
                    } else {
                        Log.w(TAG, "No historical weather data available");
                    }

                    List<DailyWeather> forecast = forecastWeathers.get();
                    if (forecast != null && !forecast.isEmpty()) {
                        Log.d(TAG, "Forecast weather data retrieved: " + forecast.size() + " entries");
                        forecast.forEach(weather ->
                                Log.d(TAG, "Forecast entry date: " + weather.getDatetime())
                        );
                        combinedList.addAll(forecast);
                    } else {
                        Log.w(TAG, "No forecast weather data available");
                    }

                    if (combinedList.isEmpty()) {
                        Log.w(TAG, "No weather data available at all");
                        dailyWeatherData.postValue(new ArrayList<>());
                        return;
                    }

                    // Sử dụng LinkedHashMap để loại bỏ các giá trị trùng lặp và giữ thứ tự
                    Map<String, DailyWeather> uniqueWeatherMap = new LinkedHashMap<>();
                    combinedList.forEach(weather -> {
                        String datetime = weather.getDatetime();
                        // Nếu ngày đã tồn tại, chỉ ghi đè nếu đó là dữ liệu dự báo
                        if (!uniqueWeatherMap.containsKey(datetime) ||
                                forecast.contains(weather)) {
                            uniqueWeatherMap.put(datetime, weather);
                        }
                    });

                    // Chuyển Map trở lại thành List
                    List<DailyWeather> filteredList = new ArrayList<>(uniqueWeatherMap.values());

                    // Sắp xếp lại theo datetime
                    Collections.sort(filteredList, Comparator.comparing(DailyWeather::getDatetime));

                    Log.d(TAG, "Combined, filtered and sorted data size: " + filteredList.size());
                    dailyWeatherData.postValue(filteredList);

                } catch (Exception e) {
                    Log.e(TAG, "Error processing weather data", e);
                    dailyWeatherData.postValue(new ArrayList<>());
                }
            }
        };

        // Historical data callback
        historyCall.enqueue(new Callback<DailyWeatherResponse>() {
            @Override
            public void onResponse(Call<DailyWeatherResponse> call, Response<DailyWeatherResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        DailyWeatherResponse weatherResponse = response.body();
                        if (weatherResponse.getData() != null && !weatherResponse.getData().isEmpty()) {
                            List<DailyWeather> weatherList = weatherResponse.getData().stream()
                                    .map(data -> convertResponseToDailyWeather(data))
                                    .collect(Collectors.toList());
                            historicalWeathers.set(weatherList);  // Store in historical data
                            Log.d(TAG, "Historical API call successful, retrieved " + weatherList.size() + " entries");
                        } else {
                            Log.w(TAG, "Historical API response body or data is null/empty");
                        }
                    } else {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Historical weather API error: " + errorBody);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing historical weather response", e);
                } finally {
                    completedCalls.incrementAndGet();
                    combineAndDeliverData.run();
                }
            }

            @Override
            public void onFailure(Call<DailyWeatherResponse> call, Throwable t) {
                Log.e(TAG, "Historical weather API call failed", t);
                completedCalls.incrementAndGet();
                combineAndDeliverData.run();
            }
        });

        // Forecast data callback
        forecastCall.enqueue(new Callback<DailyWeatherResponse>() {
            @Override
            public void onResponse(Call<DailyWeatherResponse> call, Response<DailyWeatherResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        DailyWeatherResponse weatherResponse = response.body();
                        if (weatherResponse.getData() != null && !weatherResponse.getData().isEmpty()) {
                            List<DailyWeather> weatherList = weatherResponse.getData().stream()
                                    .map(data -> convertResponseToDailyWeather(data))
                                    .collect(Collectors.toList());
                            forecastWeathers.set(weatherList);  // Store in forecast data
                            Log.d(TAG, "Forecast API call successful, retrieved " + weatherList.size() + " entries");
                        } else {
                            Log.w(TAG, "Forecast API response body or data is null/empty");
                        }
                    } else {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Forecast weather API error: " + errorBody);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing forecast weather response", e);
                } finally {
                    completedCalls.incrementAndGet();
                    combineAndDeliverData.run();
                }
            }

            @Override
            public void onFailure(Call<DailyWeatherResponse> call, Throwable t) {
                Log.e(TAG, "Forecast weather API call failed", t);
                completedCalls.incrementAndGet();
                combineAndDeliverData.run();
            }
        });

        return dailyWeatherData;
    }

    public static String getNextDate(String curDate) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = format.parse(curDate);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return format.format(calendar.getTime());
    }

    public static String getThreeDaysBefore(String curDate) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -4);
        return format.format(calendar.getTime());
    }

    private Weather convertResponseToWeather(WeatherData data) {
        Weather weather = new Weather();
        weather.setCityName(data.getCityName());
        weather.setLat(data.getLat());
        weather.setLon(data.getLon());
        weather.setDatetime(data.getDatetime());
        weather.setDescription(data.getWeather().getDescription());
        weather.setTemperature(data.getTemperature());
        weather.setHumidity(data.getHumidity());
        weather.setPrecipProbability(data.getPrecipProbability());
        weather.setWindSpeed(data.getWindSpeed());
        weather.setIcon(data.getWeather().getIcon());
        return weather;
    }

    private HourlyWeather convertResponseToHourlyWeather(HourlyWeatherData data) {
        HourlyWeather hourlyWeather = new HourlyWeather();
        hourlyWeather.setDatetime(data.getDatetime());
        hourlyWeather.setTemperature(data.getTemp());
        hourlyWeather.setIcon(data.getWeather().getIcon());
        return hourlyWeather;
    }

    private DailyWeather convertResponseToDailyWeather(DailyWeatherData data) {
        DailyWeather dailyWeather = new DailyWeather();
        dailyWeather.setDatetime(data.getDatetime());
        dailyWeather.setTemp(data.getTemp());
        return dailyWeather;
    }

    private boolean isNetworkAvailable(Context context) {
        // Implement logic to check network availability
        return true;
    }

    private void showNoInternetDialog() {
        // Implement logic to show no internet dialog
    }
}



