package Model.currentWeather;

import com.google.gson.annotations.SerializedName;

public class WeatherDescription {
    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String icon;

    // Getters and setters

    public WeatherDescription() {}

    public WeatherDescription(String description, String icon) {
        this.description = description;
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
