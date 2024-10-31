package api;

import Model.currentWeather.WeatherResponse;
import Model.dailyWeather.DailyWeather;
import Model.dailyWeather.DailyWeatherResponse;
import Model.hourlyWeather.HourlyWeather;
import Model.hourlyWeather.HourlyWeatherResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {

    //current weather
    @GET("current")
    Call<WeatherResponse> getCurrentWeatherByCity(
            @Query("city") String city,
            @Query("key") String apiKey
    );

    @GET("current")
    Call<WeatherResponse> getCurrentWeatherByLocation(
            @Query("lat") Double latitude,
            @Query("lon") Double longitude,
            @Query("key") String apiKey
    );

    //hourly weather
    //get history weather hourly, use for today weather
    @GET("history/hourly")
    Call<HourlyWeatherResponse> getHourlyHistoryByCity(
            @Query("city") String city,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("key") String apiKey
    );

    @GET("history/hourly")
    Call<HourlyWeatherResponse> getHourlyHistoryByLocation(
            @Query("lat") Double latitude,
            @Query("lon") Double longitude,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("key") String apiKey
    );

    //get forecast weather hourly, use for future weather
    @GET("forecast/hourly")
    Call<HourlyWeatherResponse> getHourlyForecastByCity(
            @Query("city") String city,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("key") String apiKey
    );

    @GET("forecast/hourly")
    Call<HourlyWeatherResponse> getHourlyForecastByLocation(
            @Query("lat") Double latitude,
            @Query("lon") Double longitude,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("key") String apiKey
    );

    //daily weather
    //get forecast weather daily, use for future weather
    @GET("forecast/daily")
    Call<DailyWeatherResponse> getDailyForecastByCity(
            @Query("city") String city,
            @Query("days") int days,
            @Query("key") String apiKey
    );

    @GET("forecast/daily")
    Call<DailyWeatherResponse> getDailyForecastByLocation(
            @Query("lat") Double latitude,
            @Query("lon") Double longitude,
            @Query("days") int days,
            @Query("key") String apiKey
    );

    //get history weather daily, use for past weather
    @GET("history/daily")
    Call<DailyWeatherResponse> getHistoryDailyByCity(
            @Query("city") String city,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("key") String apiKey
    );

    @GET("history/daily")
    Call<DailyWeatherResponse> getHistoryDailyByLocation(
            @Query("lat") Double latitude,
            @Query("lon") Double longitude,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("key") String apiKey
    );
}
