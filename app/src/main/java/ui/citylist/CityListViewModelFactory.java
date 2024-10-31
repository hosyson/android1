package ui.citylist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CityListViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public CityListViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CityListViewModel.class)) {
            return (T) new CityListViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
