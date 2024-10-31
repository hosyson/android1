package Model.currentWeather;

import com.google.gson.annotations.SerializedName;

public class WeatherData {
    @SerializedName("city_name")
    private String cityName;

    @SerializedName("lat")
    private Double lat;

    @SerializedName("lon")
    private Double lon;

    @SerializedName("datetime")
    private String datetime;

    @SerializedName("weather")
    private WeatherDescription weather;

    @SerializedName("temp")
    private double temperature;

    @SerializedName("rh")
    private int humidity;

    @SerializedName("precip")
    private double precipProbability;

    @SerializedName("wind_spd")
    private double windSpeed;

    public WeatherData() {}

    public WeatherData(String cityName, Double lat, Double lon, String datetime, WeatherDescription weather, double temperature, int humidity, double precipProbability, double windSpeed) {
        this.cityName = cityName;
        this.lat = lat;
        this.lon = lon;
        this.datetime = datetime;
        this.weather = weather;
        this.temperature = temperature;
        this.humidity = humidity;
        this.precipProbability = precipProbability;
        this.windSpeed = windSpeed;
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

    public WeatherDescription getWeather() {
        return weather;
    }

    public void setWeather(WeatherDescription weather) {
        this.weather = weather;
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
}