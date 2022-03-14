package com.example.weatherm;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeatherUtil {

    // kelvin 온도를 celsius 온도로 변환
    public static String getCelsius(double kelvin) {
        int iKelvin = (int) kelvin;
        return String.valueOf(iKelvin - 273);
    }

    // weatherId에 따라 icon 리턴
    public static int getWeatherIcon(int weatherId) {
        // 2xx thunderstorm
        if (weatherId >= 200 && weatherId < 300) {
            return R.drawable.ic_thunderstrom;
        }
        // 3xx drizzle
        else if (weatherId >= 300 && weatherId < 400) {
            return R.drawable.ic_drizzle;
        }
        // 5xx rain
        else if (weatherId >= 500 && weatherId < 600) {
            return R.drawable.ic_rain;
        }
        // 6xx snow
        else if (weatherId >= 600 && weatherId < 700) {
            return R.drawable.ic_rain;
        }
        // 7xx atmosphere
        else if (weatherId >= 700 && weatherId < 800) {
            return R.drawable.ic_rain;
        }
        // 800 clear
        else if (weatherId == 800) {
            return R.drawable.ic_clear;
        }
        // 80x clouds
        else if (weatherId > 800 && weatherId < 810) {
            return R.drawable.ic_clouds;
        }
        // 예외처리
        else {
            return R.drawable.ic_smile;
        }
    }

    // ex) 오후 11시
    // 대한민국은 time 보다 9시간 빠름
    public static String getHour(long time) {
        time = time * 1000;

        String result = "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        Date koreaDate = new Date();
        koreaDate.setTime(calendar.getTimeInMillis());

        SimpleDateFormat formatH = new SimpleDateFormat("a hh", Locale.KOREA);

        result = formatH.format(koreaDate);

        Log.d("Date", "getHour: " + result);
        return result;
    }

    //일주일 요일 구하기
    public static String getDay(long time) {
        time = time * 1000;
        String result = "";

        Calendar toDay = Calendar.getInstance();
        toDay.setTimeInMillis(time);

        int day_to_week = toDay.get(Calendar.DAY_OF_WEEK);
        Log.d("Date", "getDay: " + day_to_week);

        if (day_to_week == 1)
            result = "일요일";
        if (day_to_week == 2)
            result = "월요일";
        if (day_to_week == 3)
            result = "화요일";
        if (day_to_week == 4)
            result = "수요일";
        if (day_to_week == 5)
            result = "목요일";
        if (day_to_week == 6)
            result = "금요일";
        if (day_to_week == 7)
            result = "토요일";

        return result;
    }

    //일주일 날짜 구하기
    public static String getWeekly(long time) {
        time = time * 1000;
        String result = "";

        //오늘 날짜부터 5일 후 날짜 구하기
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        Date koreaDate = new Date();
        koreaDate.setTime(calendar.getTimeInMillis());

        SimpleDateFormat formatM = new SimpleDateFormat("MM/dd", Locale.KOREA);

        result = formatM.format(koreaDate);

        return result;
    }
}
