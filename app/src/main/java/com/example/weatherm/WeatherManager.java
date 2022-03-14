package com.example.weatherm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.weatherm.api_retrofit.ApiManager;
import com.example.weatherm.data.ForecastData;
import com.example.weatherm.data.WeatherData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherManager {

    private Activity activity;
    private OnChangeWeather onChangeWeather;
    // 날씨
    private ApiManager apiManager;

    public WeatherManager(Activity activity, OnChangeWeather onChangeWeather) {

        this.activity = activity;
        this.onChangeWeather = onChangeWeather;

        apiManager = ApiManager.getInstance();

        // 위치 확인
        final LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한 없으면 메소드 실행 중지
            Toast.makeText(activity, "날씨 권한이 없습니다.", Toast.LENGTH_SHORT).show();

            if (activity instanceof MainActivity) {
                ((MainActivity) activity).hideProgress();
            }
            return;
        }

        //위도 경도를 가져옴.
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, null);
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, null);

        // 5분마다 or 100m 마다 위치 정보 갱신
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                30000, 100, mLocationListener);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                30000, 100, mLocationListener);
    }

    private final LocationListener mLocationListener = new LocationListener() {

        //위치가 변경(응답)되었을때, onLocationChanged 실행
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();  // 위도
            double longitude = location.getLongitude(); // 경도

            String lat = String.format("%.2f", latitude);
            String lon = String.format("%.2f", longitude);

            // api 호출
            requestWeather(lat, lon);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void requestWeather(final String lat, final String lon) {

        // OkHttp의 Call 이랑 클래스명이 겹쳐서 retrofit2.Call 을 써준것

        // Seoul의 날씨 요청
//        retrofit2.Call<String> response = apiManager.getWeather("Seoul");

        // 위도 경도로 날씨 요청
        Call<WeatherData> response = apiManager.getWeatherByLatitude(lat, lon);

        response.enqueue(new Callback<WeatherData>() {
            // 응답 성공
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                Log.d("Retrofit", "requestWeather : onResponse");
//                onChangeWeather.change(response.body());
                WeatherData weatherData = response.body();

                // 시간별 날씨 요청
                requestForecast(weatherData, lat, lon);
            }

            // 응답 실패
            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.d("Retrofit", "requestWeather : onFailure");
                Toast.makeText(activity, "네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();

                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).hideProgress();
                }
            }
        });
    }

    private void requestForecast(final WeatherData weatherData , String lat, String lon) {
        // 3시간별 날씨 요청
        Call<ForecastData> response = apiManager.getForecastByLatitude(lat, lon);
        response.enqueue(new Callback<ForecastData>() {
            @Override
            public void onResponse(Call<ForecastData> call, Response<ForecastData> response) {
                Log.d("Retrofit", "requestForecast : onResponse");

                ForecastData forecastData = response.body();

                onChangeWeather.change(weatherData, forecastData);
            }

            @Override
            public void onFailure(Call<ForecastData> call, Throwable t) {
                Log.d("Retrofit", "requestForecast : onFailure");
                Log.d("Retrofit", "requestForecast error message : " + t.getMessage());
                Toast.makeText(activity, "네트워크를 확인해주세요.", Toast.LENGTH_SHORT).show();

                //다이얼로그 종료
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).hideProgress();
                }
            }
        });
    }

    /*
    private void initOkHttp() {
        // 통신을 할 땐, manifest에 인터넷 허용 권한을 넣어줘야함
        // OkHttp로 통신 시작
        HttpConnection.getInstance().requestWebServer(new Callback() {

            // 응답 실패했을 때 실행되는 콜백 메소드
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("HttpConnection", "onFailure");
            }

            // 응답 성공했을 때 실행되는 콜백 메소드
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("HttpConnection", "onResponse");

                // 응답받는 response 데이터의 body 를 로그출력
                Log.d("HttpConnection", "response : " + response.body().string());
            }
        });
        // OkHttp로 통신 끝
    }
     */

    public interface OnChangeWeather {
        void change(WeatherData weatherData, ForecastData forecastData);
    }
}
