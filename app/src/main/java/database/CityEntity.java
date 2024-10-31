package database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "city_table")
public class CityEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String country;
    private double latitude;
    private double longitude;

    public CityEntity(String name, String country, double latitude, double longitude) {
        this.name = name;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}