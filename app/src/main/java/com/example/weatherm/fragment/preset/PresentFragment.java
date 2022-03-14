package com.example.weatherm.fragment.preset;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherm.MainActivity;
import com.example.weatherm.R;
import com.example.weatherm.data.ForecastData;
import com.example.weatherm.data.WeatherData;
import com.example.weatherm.WeatherUtil;

import java.util.ArrayList;
import java.util.List;


public class PresentFragment extends Fragment {

    private MainActivity activity;
    private PresentAdapter presentAdapter;

    private RecyclerView recyclerView;
    private TextView presentWeatherTemperature; //온도
    private TextView presentWeatherCity; //도시
    private ImageView presentWeatherIcon; //날씨 이미지

    private WeatherData weatherData;
    private ForecastData forecastData;

    // 3시간별로 출력할 날씨 개수
    private final int WEATHER_COUNT = 10;

    //context 를 매개변수로 받아 context 를 MainActivity 타입으로 강제 형변환
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //view 를 inflater 을 하여 객체화 시킴
        View view = inflater.inflate(R.layout.fragment_present, null);

        //view 의 instance(객체) 를 얻어옴.
        presentWeatherTemperature = view.findViewById(R.id.present_weather_temperature);
        presentWeatherCity = view.findViewById(R.id.present_weather_city);
        presentWeatherIcon = view.findViewById(R.id.present_weather_icon);
        recyclerView = view.findViewById(R.id.present_recyclerview);

        weatherData = activity.getWeatherData();
        forecastData = activity.getForecastData();

        if (weatherData != null) {
            loadTop();
        }

        if (forecastData != null) {
            loadBottom();
        }

        return view;
    }

    private void loadTop() {
        // 현재 도시 이름
        presentWeatherCity.setText(weatherData.getName());

        // 현재 날씨 이미지
        int weatherId = weatherData.getWeather().get(0).getId();
        int imageResource = WeatherUtil.getWeatherIcon(weatherId);
        presentWeatherIcon.setImageResource(imageResource);

        // 현재 온도
        String temp = WeatherUtil.getCelsius(weatherData.getMain().getTemp());
        presentWeatherTemperature.setText(temp + "˚");

    }

    private void loadBottom() {
        List<ForecastData.ListBean> forecastList = new ArrayList<>();

        for (int i = 0; i < WEATHER_COUNT; i++) {
            forecastList.add(forecastData.getList().get(i));
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //생성자로 forecastList 데이터 전달
        presentAdapter = new PresentAdapter(forecastList);
        recyclerView.setAdapter(presentAdapter);
    }
}
