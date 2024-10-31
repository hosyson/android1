package Model.dailyWeather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import Model.hourlyWeather.HourlyWeatherData;

public class DailyWeatherResponse {
    @SerializedName("data")
    private List<DailyWeatherData> data;

    public List<DailyWeatherData> getData() { return data; }

    public void setData(List<DailyWeatherData> data) { this.data = data; }

    public DailyWeatherResponse() {}

    public DailyWeatherResponse(List<DailyWeatherData> data) {
        this.data = data;
    }
}
