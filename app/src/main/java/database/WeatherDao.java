package database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WeatherDao {

    // Thêm một bản ghi thời tiết, nếu trùng thì ghi đè
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWeather(WeatherEntity weather);

    // Thêm nhiều bản ghi thời tiết, nếu trùng thì ghi đè
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllWeather(List<WeatherEntity> weatherList);

    // Cập nhật bản ghi thời tiết đã có
    @Update
    void updateWeather(WeatherEntity weather);

    // Xóa một bản ghi thời tiết
    @Delete
    void deleteWeather(WeatherEntity weather);

    // Xóa tất cả dữ liệu thời tiết
    @Query("DELETE FROM weather")
    void deleteAllWeather();

    // Lấy bản ghi thời tiết mới nhất
    @Query("SELECT * FROM weather ORDER BY date DESC LIMIT 1")
    static WeatherEntity getLatestWeather() {
        return null;
    }

    // Lấy dữ liệu thời tiết cho 11 ngày gần nhất
    @Query("SELECT * FROM weather ORDER BY date DESC LIMIT 11")
    List<WeatherEntity> getWeatherFor11Days();

    // Lấy tất cả bản ghi thời tiết
    @Query("SELECT * FROM weather ORDER BY date DESC")
    List<WeatherEntity> getAllWeather();
}
