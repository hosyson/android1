package utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class LocationManager {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final FusedLocationProviderClient fusedLocationClient;
    private final Context context;
    private Location currentLocation;
    private TimeZone currentTimeZone;

    public LocationManager(Context context) {
        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void updateCurrentLocation(OnSuccessListener<Location> listener) {
        if (!hasLocationPermission()) {
            Log.d("LocationManager", "No location permission");
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            Log.d("LocationManager", "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                            currentLocation = location;
                            updateTimeZone();
                            if (listener != null) {
                                listener.onSuccess(location);
                            }
                        } else {
                            Log.d("LocationManager", "Location is null");
                            requestLocationUpdates(listener);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("LocationManager", "Error getting location", e);
                        requestLocationUpdates(listener);
                    });
        } catch (SecurityException e) {
            Log.e("LocationManager", "Security exception when requesting location", e);
        }
    }

    private void requestLocationUpdates(OnSuccessListener<Location> listener) {
        if (!hasLocationPermission()) return;

        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)
                    .setFastestInterval(5000)
                    .setNumUpdates(1);

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult != null && locationResult.getLastLocation() != null) {
                                currentLocation = locationResult.getLastLocation();
                                updateTimeZone();
                                if (listener != null) {
                                    listener.onSuccess(currentLocation);
                                }
                            }
                        }
                    },
                    Looper.getMainLooper());
        } catch (SecurityException e) {
            Log.e("LocationManager", "Security exception when requesting location updates", e);
        }
    }

    private void updateTimeZone() {
        if (currentLocation != null) {
            try {
                Geocoder geocoder = new Geocoder(context);
                List<Address> addresses = geocoder.getFromLocation(
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude(),
                        1
                );

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String countryCode = address.getCountryCode();
                    String timezoneId = getTimezoneId(address.getLocality(), countryCode);
                    currentTimeZone = TimeZone.getTimeZone(timezoneId);
                    Log.d("LocationManager", "TimeZone updated: " + timezoneId);
                }
            } catch (IOException e) {
                Log.e("LocationManager", "Error getting timezone", e);
                // Fallback to device timezone
                currentTimeZone = TimeZone.getDefault();
            }
        }
    }

    private String getTimezoneId(String city, String countryCode) {
        // Simplified timezone mapping based on country codes
        // You can expand this mapping based on your needs
        Map<String, String> timezoneMap = new HashMap<>();
        timezoneMap.put("US", "America/New_York");  // Default US timezone
        timezoneMap.put("GB", "Europe/London");
        timezoneMap.put("JP", "Asia/Tokyo");
        timezoneMap.put("CN", "Asia/Shanghai");
        timezoneMap.put("AU", "Australia/Sydney");
        timezoneMap.put("VN", "Asia/Ho_Chi_Minh");
        // Add more country codes and their default timezones as needed

        String timezoneId = timezoneMap.get(countryCode);
        return timezoneId != null ? timezoneId : TimeZone.getDefault().getID();
    }

    public Double getLatitude() {
        return (currentLocation != null) ? currentLocation.getLatitude() : null;
    }

    public Double getLongitude() {
        return (currentLocation != null) ? currentLocation.getLongitude() : null;
    }

    public Date getCurrentLocalDateTime() {
        TimeZone tz = currentTimeZone != null ? currentTimeZone : TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(tz);
        return calendar.getTime();
    }

    public String getCurrentLocalDateFormatted(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        sdf.setTimeZone(currentTimeZone != null ? currentTimeZone : TimeZone.getDefault());
        return sdf.format(getCurrentLocalDateTime());
    }

    public String getCurrentLocalDate() {
        return getCurrentLocalDateFormatted("yyyy-MM-dd");
    }

    public String getCurrentLocalTime() {
        return getCurrentLocalDateFormatted("HH:mm:ss");
    }

    public String getCurrentLocalDateTimeISO() {
        return getCurrentLocalDateFormatted("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    public TimeZone getCurrentTimeZone() {
        return currentTimeZone != null ? currentTimeZone : TimeZone.getDefault();
    }

    public Calendar getCalendarForLocation() {
        return Calendar.getInstance(getCurrentTimeZone());
    }

    public String getThreeDaysBefore() {
        Calendar calendar = getCalendarForLocation();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(getCurrentTimeZone());
        return sdf.format(calendar.getTime());
    }

    public String getDateOffset(int days) {
        Calendar calendar = getCalendarForLocation();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setTimeZone(getCurrentTimeZone());
        return sdf.format(calendar.getTime());
    }
}

