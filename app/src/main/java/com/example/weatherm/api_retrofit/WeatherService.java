package com.example.weatherm.api_retrofit;

import com.example.weatherm.data.WeatherData;
import com.example.weatherm.data.ForecastData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface WeatherService {
    //        https://api.openweathermap.org/data/2.5/weather?q=Seoul&APPID=10465524bfe7eaa71c4010cb81b84a11
    @GET("data/2.5/weather")
    Call<WeatherData> getWeather(@QueryMap Map<String, String> options);

    @GET("data/2.5/forecast")
    Call<ForecastData> getForecast(@QueryMap Map<String, String> options);

}
