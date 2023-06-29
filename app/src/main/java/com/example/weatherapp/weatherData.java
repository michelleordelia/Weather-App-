package com.example.weatherapp;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class weatherData {
    private String mTemperature, mLocation, mDate, mIcon, mWeatherType, mWind, mHumidity, mRain;
    private int mCondition;

    public static weatherData fromJson(JSONObject jsonObject){
        try {
            weatherData weatherD = new weatherData();
            weatherD.mLocation = jsonObject.getString("name");
            weatherD.mDate = jsonObject.getString("dt");
            weatherD.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherD.mWeatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherD.mIcon = updateWeatherIcon(weatherD.mCondition);

            double tempData = jsonObject.getJSONObject("main").getDouble("temp")-273.15;
            int tempRounded = (int) Math.rint(tempData);
            weatherD.mTemperature = Integer.toString(tempRounded);

            double humidityData = jsonObject.getJSONObject("main").getDouble("humidity");
            weatherD.mHumidity = Integer.toString((int) humidityData);

            // Check if "wind" key exists
            if (jsonObject.has("wind")) {
                weatherD.mWind = String.valueOf(jsonObject.getJSONObject("wind").getDouble("speed"));
            } else {
                weatherD.mWind = "N/A"; // "wind" key is not present
            }

            // Check if "rain" key exists
            if (jsonObject.has("rain")) {
                weatherD.mRain = String.valueOf(jsonObject.getJSONObject("rain").getDouble("1h"));
            } else {
                weatherD.mRain = "N/A"; // "rain" key is not present
            }


            return weatherD;
        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String updateWeatherIcon(int condition){
        if (condition >= 0 && condition < 300){
            return "thunderstorm";
        } else if (condition >= 300 && condition < 500){
            return "rainwithsun";
        } else if (condition >= 500 && condition < 600) {
            return "showerrain";
        } else if (condition >= 600 && condition < 700) {
            return "snow";
        } else if (condition >= 701 && condition < 800) {
            return "mist";
        } else if (condition == 800) {
            return "clear";
        } else if (condition >= 801 && condition <= 802) {
            return "scatteredclouds";
        } else if (condition >= 803 && condition <= 804) {
            return "brokenclouds";
        }
        return "not specified";
    }

    public String getmTemperature() {
        return mTemperature + "°C";
    }

    public String getmLocation() {
        return mLocation;
    }

    public String getmDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault());
        Date date = new Date(Long.parseLong(mDate) * 1000); // Convert timestamp to milliseconds
        return dateFormat.format(date);
    }

    public String getmIcon() {
        return mIcon;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }

    public String getmWind() {
        return "Wind: " + mWind + "m/s";
    }

    public String getmHumidity() {
        return "Humidity: " + mHumidity + "%";
    }


    public String getmRain() {
        if (mRain.equals("N/A")){
            return "Rainfall: " + mRain;
        } else
            return "Rainfall: " + mRain + "mm";

    }
}
