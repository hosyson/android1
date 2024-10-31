package database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import database.CityEntity;

@Dao
public interface CityDao {
    @Insert
    void insert(CityEntity city);

    @Query("SELECT * FROM city_table")
    LiveData<List<CityEntity>> getAllCities();

    @Query("DELETE FROM city_table")
    void deleteAll();
}
