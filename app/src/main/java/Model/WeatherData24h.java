package Model;

public class WeatherData24h {
    public String Day;
    public String Status;
    public String Image;
    public String MaxTemp;
    public String MinTemp;

    public WeatherData24h(String day,String status,String image,String hour,String minTemp) {
        Day=day;
        Status=status;
        Image=image;
        MaxTemp=hour;
        MinTemp=minTemp;
    }
}


