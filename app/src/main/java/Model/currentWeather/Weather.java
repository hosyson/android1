package Model.currentWeather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import Model.dailyWeather.DailyWeather;
import Model.hourlyWeather.HourlyWeather;

public class Weather {
    @SerializedName("city_name")
    private String cityName;

    @SerializedName("lat")
    private Double lat;

    @SerializedName("lon")
    private Double lon;

    @SerializedName("datetime")
    private String datetime;

    @SerializedName("weather_description")
    private String description;

    @SerializedName("temp")  // or could be "current_temp" depending on API response
    private double temperature;

    @SerializedName("rh")  // relative humidity in API
    private int humidity;

    @SerializedName("precip")  // or "pop" for probability of precipitation
    private double precipProbability;

    @SerializedName("wind_spd")  // wind speed in API
    private double windSpeed;

    @SerializedName("weather_icon")
    private String icon;

    @SerializedName("hourly")  // adjust based on actual API response
    private List<HourlyWeather> hourlyForecast;

    @SerializedName("daily")   // adjust based on actual API response
    private List<DailyWeather> dailyForecast;

    // Keep existing constructors
    public Weather() {}

    public Weather(String cityName, Double lat, Double lon, String datetime, String description,
                   double temperature, int humidity, double precipProbability, double windSpeed,
                   String icon, List<HourlyWeather> hourlyForecast, List<DailyWeather> dailyForecast) {
        this.cityName = cityName;
        this.lat = lat;
        this.lon = lon;
        this.datetime = datetime;
        this.description = description;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipProbability = precipProbability;
        this.windSpeed = windSpeed;
        this.icon = icon;
        this.hourlyForecast = hourlyForecast;
        this.dailyForecast = dailyForecast;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
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

    public List<HourlyWeather> getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(List<HourlyWeather> hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }

    public List<DailyWeather> getDailyForecast() {
        return dailyForecast;
    }

    public void setDailyForecast(List<DailyWeather> dailyForecast) {
        this.dailyForecast = dailyForecast;
    }
}
