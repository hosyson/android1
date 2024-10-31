package ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import utils.LocationManager;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private LocationManager locationManager;

    public HomeViewModelFactory(Application application, LocationManager locationManager) {
        this.application = application;
        this.locationManager = locationManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(application, locationManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
