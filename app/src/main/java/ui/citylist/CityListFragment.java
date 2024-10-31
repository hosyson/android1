package ui.citylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;

import java.util.ArrayList;

import database.CityEntity;
import ui.citylist.CityListViewModelFactory;

public class CityListFragment extends Fragment {

    private CityAdapter adapter;
    private CityListViewModel cityListViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.cityList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CityAdapter(new ArrayList<CityEntity>(), city -> {
            cityListViewModel.fetchWeatherDataForCity(city.getName());
        });
        recyclerView.setAdapter(adapter);

        CityListViewModelFactory factory = new CityListViewModelFactory(requireActivity().getApplication());
        cityListViewModel = new ViewModelProvider(this, factory).get(CityListViewModel.class);

        cityListViewModel.getCityList().observe(getViewLifecycleOwner(), cities -> {
            if (cities != null && !cities.isEmpty()) {
                adapter.updateCities(cities);
            }
        });

        cityListViewModel.getWeatherData().observe(getViewLifecycleOwner(), weather -> {
            if (weather != null) {
                String weatherInfo = weather.getCityName() + ": " +
                        weather.getTemperature() + "°C, " +
                        weather.getWeather().getDescription() + ", " +
                        "Humidity: " + weather.getHumidity() + "% " +
                        "Wind: " + weather.getWindSpeed() + " m/s";

                Toast.makeText(getContext(), weatherInfo, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
