package database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "weather")
public class WeatherEntity {

    @PrimaryKey
    @NonNull
    public String date;
    public String city_name;
    public double lat;
    public double lon;
    public double current_temp;
    public int humidity;
    public double precipProbability;
    public double windSpeed;
    private String icon;
    public String weather_description;
    public String hourly_forecast;// Store as JSON string
    public String daily_forecast;// Store as JSON string
    public int aqi; // Air Quality Index

    // Constructor

    public WeatherEntity() {}

    public WeatherEntity(@NonNull String date, String city_name, double lat, double lon, double current_temp, int humidity, double precipProbability, double windSpeed, String icon, String weather_description, String hourly_forecast, String daily_forecast, int aqi) {
        this.date = date;
        this.city_name = city_name;
        this.lat = lat;
        this.lon = lon;
        this.current_temp = current_temp;
        this.humidity = humidity;
        this.precipProbability = precipProbability;
        this.windSpeed = windSpeed;
        this.icon = icon;
        this.weather_description = weather_description;
        this.hourly_forecast = hourly_forecast;
        this.daily_forecast = daily_forecast;
        this.aqi = aqi;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getCurrent_temp() {
        return current_temp;
    }

    public void setCurrent_temp(double current_temp) {
        this.current_temp = current_temp;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWeather_description() {
        return weather_description;
    }

    public void setWeather_description(String weather_description) {
        this.weather_description = weather_description;
    }

    public String getHourly_forecast() {
        return hourly_forecast;
    }

    public void setHourly_forecast(String hourly_forecast) {
        this.hourly_forecast = hourly_forecast;
    }

    public String getDaily_forecast() {
        return daily_forecast;
    }

    public void setDaily_forecast(String daily_forecast) {
        this.daily_forecast = daily_forecast;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }
}
