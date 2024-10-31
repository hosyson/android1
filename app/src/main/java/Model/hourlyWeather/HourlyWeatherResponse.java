package Model.hourlyWeather;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HourlyWeatherResponse {
    @SerializedName("data")
    private List<HourlyWeatherData> data;

    public HourlyWeatherResponse() {
        this.data = new ArrayList<>();
    }

    public HourlyWeatherResponse(List<HourlyWeatherData> data) {
        this.data = data;
    }

    public List<HourlyWeatherData> getData() {
        return data;
    }

    public void setData(List<HourlyWeatherData> data) {
        this.data = data;
    }
}
