package Model.hourlyWeather;

public class HourlyWeather {
    private String datetime;

    private double temperature;

    private String icon;

    // Constructors
    public HourlyWeather() {}

    public HourlyWeather(String datetime, double temperature, String icon) {
        this.datetime = datetime;
        this.temperature = temperature;
        this.icon = icon;
    }

    // Getters and Setters
    public String getDatetime() { return datetime; }
    public void setDatetime(String datetime) { this.datetime = datetime; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getHour() { return datetime.substring(datetime.lastIndexOf(":") + 1); }

}