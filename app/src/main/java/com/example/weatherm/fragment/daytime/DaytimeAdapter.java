package com.example.weatherm.fragment.daytime;

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

public class DaytimeAdapter extends RecyclerView.Adapter<DaytimeAdapter.DaytimeHolder> {

    private List<ForecastData.ListBean> forecastList;

    //Adapter 에 전잘해 사용할 List 형의 데이터
    public DaytimeAdapter(List<ForecastData.ListBean> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public DaytimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //view 실체화
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weekly_item, parent, false);
        //view 를 holder 에 넣어줌
        DaytimeHolder holder = new DaytimeHolder(view);

        return holder;
    }

    //onCreateViewHolder() 에서 holder 를 return 받은 holder 에 데이터를 셋팅해준다.
    @Override
    public void onBindViewHolder(@NonNull DaytimeHolder holder, int position) {

        ForecastData.ListBean forecastBean = forecastList.get(position);

        //요일
        long toDay = forecastBean.getDt();
        String textWeekly = WeatherUtil.getDay(toDay);
        holder.daytime_weather_weekly.setText(textWeekly);

        //날짜
        String textDate = WeatherUtil.getWeekly(toDay);
        holder.daytime_weather_date.setText(textDate);

        //날씨 이미지
        int weatherId = forecastBean.getWeather().get(0).getId();
        int imageResource = WeatherUtil.getWeatherIcon(weatherId);
        holder.daytime_weather_icon.setImageResource(imageResource);

        String high = WeatherUtil.getCelsius(forecastBean.getMain().getTemp_max());
        holder.daytime_weather_hightemperature.setText(high + "˚");

        String low = WeatherUtil.getCelsius(forecastBean.getMain().getTemp_min());
        holder.daytime_weather_lowtemperature.setText(low + "˚");
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    class DaytimeHolder extends RecyclerView.ViewHolder {

        private TextView daytime_weather_weekly;
        private TextView daytime_weather_date;
        private ImageView daytime_weather_icon;
        private TextView daytime_weather_hightemperature;
        private TextView daytime_weather_lowtemperature;

        public DaytimeHolder(@NonNull View itemView) {
            super(itemView);
            //요일
            daytime_weather_weekly = itemView.findViewById(R.id.daytime_weather_weekly);
            //날짜
            daytime_weather_date = itemView.findViewById(R.id.daytime_weather_date);
            //이미지 날씨
            daytime_weather_icon = itemView.findViewById(R.id.daytime_weather_icon);
            //최고온도
            daytime_weather_hightemperature = itemView.findViewById(R.id.daytime_weather_hightemperature);
            //최저온도
            daytime_weather_lowtemperature = itemView.findViewById(R.id.daytime_weather_lowtemperature);
        }
    }
}
