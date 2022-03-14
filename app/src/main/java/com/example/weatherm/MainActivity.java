package com.example.weatherm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import com.example.weatherm.data.ForecastData;
import com.example.weatherm.data.WeatherData;
import com.example.weatherm.fragment.MyPagerAdapter;
import com.example.weatherm.fragment.preset.PresentFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, MainActivity.class);
    }

    // 사용자의 입력을 제한
    // api 요청시에 사용
    private ProgressDialog pd;

    private ViewPager viewPager;

    private WeatherManager weatherManager;
    private WeatherData weatherData;
    private ForecastData forecastData;

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }

    public ForecastData getForecastData() {
        return forecastData;
    }

    public void setForecastData(ForecastData forecastData) {
        this.forecastData = forecastData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.pager);

        showProgress("날씨 요청 중입니다.");

        weatherManager = new WeatherManager(this, new WeatherManager.OnChangeWeather() {
            @Override
            public void change(WeatherData weatherData, ForecastData forecastData) {
                // api 응답 완료시 실행
                hideProgress();

                setWeatherData(weatherData);
                setForecastData(forecastData);

                init();
            }
        });

//        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(adapter);
//
//        TabLayout tabLayout = findViewById(R.id.tab);
//        tabLayout.setupWithViewPager(viewPager);
    }

    private void init(){
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
    }

    //ProgressDialog 출력 메소드
    public void showProgress(String message) {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }

        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    //다이얼로그 종료 메소드
    public void hideProgress() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }
}
