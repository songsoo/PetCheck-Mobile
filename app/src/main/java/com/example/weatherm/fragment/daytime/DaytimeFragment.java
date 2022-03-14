package com.example.weatherm.fragment.daytime;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherm.MainActivity;
import com.example.weatherm.R;
import com.example.weatherm.WeatherUtil;
import com.example.weatherm.data.ForecastData;

import java.util.ArrayList;
import java.util.List;

public class DaytimeFragment extends Fragment {

    private MainActivity activity;
    private RecyclerView recyclerView;

    private DaytimeAdapter daytimeAdapter;
    private ForecastData forecastData;

    //context 를 매개변수로 받아 context 를 MainActivity 타입으로 강제 형변환.?
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        activity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //view 를 객체화 시킴
        View view = inflater.inflate(R.layout.fragment_daytime, null);
        recyclerView = view.findViewById(R.id.daytime_recyclerview);

        forecastData = activity.getForecastData();

        if (forecastData != null) {
            loadWeekly();
        }
        return view;
    }

    private void loadWeekly() {

        // adapter에 전달할 날씨데이터리스트
        List<ForecastData.ListBean> forecastList = new ArrayList<>();
        // 현재 데이터의 날짜
        String prevDate = null;

        for (int i = 0; i < forecastData.getList().size(); i++) {

            ForecastData.ListBean forecastBean = forecastData.getList().get(i);

            if (i == 0) {
                // 현재 데이터의 시간
                long time = forecastBean.getDt();
                prevDate = WeatherUtil.getWeekly(time);
                forecastList.add(forecastBean);
            } else {
                // 현재 데이터의 시간, 날짜
                long time = forecastBean.getDt();
                String nowDate = WeatherUtil.getWeekly(time);

                // 이전 데이터의 날짜와 비교
                if (!nowDate.equalsIgnoreCase(prevDate)) {
                    prevDate = nowDate;
                    forecastList.add(forecastBean);
                }
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        daytimeAdapter = new DaytimeAdapter(forecastList);

        recyclerView.setAdapter(daytimeAdapter);
    }

}
