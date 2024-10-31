package Model.currentWeather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    @SerializedName("data")
    private List<WeatherData> data;

    public List<WeatherData> getData() {
        return data;
    }

    public void setData(List<WeatherData> data) {
        this.data = data;
    }

    public WeatherResponse() {}

    public WeatherResponse(List<WeatherData> data) {
        this.data = data;
    }

}
