package repository;
import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import database.CityDao;
import database.CityDatabase;
import database.CityEntity;

public class CityRepository {
    private CityDao cityDao;
    private LiveData<List<CityEntity>> allCities;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CityRepository(Application application) {
        CityDatabase db = CityDatabase.getDatabase(application);
        cityDao = db.cityDao();
        allCities = cityDao.getAllCities();
    }

    public LiveData<List<CityEntity>> getAllCities() {
        return allCities;
    }

    public void insert(CityEntity city) {
        executorService.execute(() -> cityDao.insert(city));
    }

    public void deleteAll() {
        executorService.execute(() -> cityDao.deleteAll());
    }
}
