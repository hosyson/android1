package Model.hourlyWeather;

import com.google.gson.annotations.SerializedName;

public class HourlyWeatherData {

    @SerializedName("app_temp")
    private double temp;

    @SerializedName("weather")
    private HourlyWeatherDescription weather;

    @SerializedName("datetime")
    private String datetime;

    public HourlyWeatherData() {}

    public HourlyWeatherData(double temp, HourlyWeatherDescription weather, String datetime) {
        this.temp = temp;
        this.weather = weather;
        this.datetime = datetime;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public HourlyWeatherDescription getWeather() {
        return weather;
    }

    public void setWeather(HourlyWeatherDescription weather) {
        this.weather = weather;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public boolean isCurrentDay() {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate weatherDate = java.time.LocalDate.parse(getDatetime());
        return today.equals(weatherDate);
    }
}
