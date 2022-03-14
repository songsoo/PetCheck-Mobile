package com.example.weatherm.fragment.preset;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherm.WeatherUtil;
import com.example.weatherm.R;
import com.example.weatherm.data.ForecastData;
import java.util.List;

public class PresentAdapter extends RecyclerView.Adapter<PresentAdapter.PresentViewHolder> {

    private List<ForecastData.ListBean> forecastList;
    //Adapter 에 전달해 사용할 List 형의 데이터

    public PresentAdapter(List<ForecastData.ListBean> forecastList) {
        this.forecastList = forecastList;
    }

    //Adapter 의 전달에 사용할 List 형의 데이터 세팅
//    public void setData(List<ForecastData.ListBean> forecastList) {
//        forecastList = forecastList;
//    }

    @NonNull
    @Override
    public PresentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //사용할 item 의 View 를 생성,
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_item, parent, false);
        PresentViewHolder holder = new PresentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PresentViewHolder holder, int position) {

        ForecastData.ListBean forecastBean = forecastList.get(position);

        //시간
        holder.today_weather_time.setText(WeatherUtil.getHour(forecastBean.getDt()));

        //이미지
        int weatherId = forecastBean.getWeather().get(0).getId();
        int imageResource = WeatherUtil.getWeatherIcon(weatherId);
        holder.today_weather_icon.setImageResource(imageResource);

        //온도
        String temp = WeatherUtil.getCelsius(forecastBean.getMain().getTemp());
        holder.today_weather_temperature.setText(temp + "˚");

    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    static class PresentViewHolder extends RecyclerView.ViewHolder {

        //레이아웃 today 타입 변수 선언
        public TextView today_weather_time;
        public ImageView today_weather_icon;
        public TextView today_weather_temperature;

        public PresentViewHolder(@NonNull View itemView) {
            super(itemView);

            today_weather_time = itemView.findViewById(R.id.today_weather_time);
            today_weather_icon = itemView.findViewById(R.id.today_weather_icon);
            today_weather_temperature = itemView.findViewById(R.id.today_weather_temperature);
        }
    }
}
