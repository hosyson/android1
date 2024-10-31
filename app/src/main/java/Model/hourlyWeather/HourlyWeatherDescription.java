package Model.hourlyWeather;

import com.google.gson.annotations.SerializedName;

public class HourlyWeatherDescription {

    @SerializedName("icon")
    private String icon;

    public HourlyWeatherDescription() {}

    public HourlyWeatherDescription(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
