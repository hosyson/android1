package Model.dailyWeather;

import com.google.gson.annotations.SerializedName;

public class DailyWeather {
    @SerializedName("datetime")
    private String datetime;

    @SerializedName("temp")
    private double temp;

    public DailyWeather() {}

    public DailyWeather(String datetime, double temp) {
        this.datetime = datetime;
        this.temp = temp;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }
}
