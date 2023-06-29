package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.service.controls.templates.TemperatureControlTemplate;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    // Declare variables
    final String API_KEY = "ddfad4bb66c501f4f453e1de3038350c";
    final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000; // 5 seconds
    final float MIN_DISTANCE = 1000; // 1 meter
    final int REQUEST_CODE = 101;

    String LOCATION_PROVIDER= LocationManager.GPS_PROVIDER;
    TextView textLocation, textDate, textTemperature, weatherType, textWind, textHumidity, textRain;
    ImageView weatherIcon;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @SuppressLint("MissingInflatedId") // !!!!
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the default locale
        Locale currentLocale = Locale.getDefault();

        textLocation = findViewById(R.id.textLocation);
        textDate = findViewById(R.id.textDate);
        textTemperature = findViewById(R.id.textTemperature);
        weatherIcon = findViewById(R.id.weatherIcon);
        weatherType = findViewById(R.id.textWeatherType);
        textWind = findViewById(R.id.textWind);
        textHumidity = findViewById(R.id.textHumidity);
        textRain = findViewById(R.id.textRain);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWeatherCurrentLocation();
    }

    // method to get weather data for current location
    private void getWeatherCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                // fetching weather data from url
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", API_KEY);
                fetchWeather(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // LocationListener.super.onStatusChanged(provider, status, extras);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                // LocationListener.super.onProviderEnabled(provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                LocationListener.super.onProviderDisabled(provider);
                // user allows the location but we are not able to get the location
            }
        };

        // check for location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
            mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    // handle location permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Location get Successfully", Toast.LENGTH_SHORT).show();
                getWeatherCurrentLocation();
            } else {
                // user denied the permission
            }
        }
    }

    // fetch weather data from the API for current location
    private void fetchWeather(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(API_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // super.onSuccess(statusCode, headers, response);
                Toast.makeText(MainActivity.this, "Data retrieved successfully", Toast.LENGTH_SHORT).show();
                weatherData mWeatherData = weatherData.fromJson(response);
                updateUI(mWeatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                // super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(MainActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // update the UI with the weather data
    private void updateUI(weatherData weather){
        textTemperature.setText(weather.getmTemperature());
        textLocation.setText(weather.getmLocation());
        weatherType.setText(weather.getmWeatherType());
        int resourceID = getResources().getIdentifier(weather.getmIcon(), "drawable", getPackageName());
        weatherIcon.setImageResource(resourceID);
        textDate.setText(weather.getmDate());
        textWind.setText(weather.getmWind());
        textHumidity.setText(weather.getmHumidity());
        textRain.setText(weather.getmRain());

    }

    @Override
    protected void onPause() {
        super.onPause();
        // remove location updates when the activity is paused
        if(mLocationManager != null){
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}