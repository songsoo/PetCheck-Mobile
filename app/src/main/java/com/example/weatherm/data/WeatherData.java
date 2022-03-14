package com.example.weatherm.data;

import java.util.List;

public class WeatherData {


    /**
     * coord : {"lon":139,"lat":35}
     * weather : [{"id":803,"main":"Clouds","description":"broken clouds","icon":"04n"}]
     * base : stations
     * main : {"temp":298.43,"pressure":1017,"humidity":70,"temp_min":298.15,"temp_max":299.26}
     * wind : {"speed":2.75,"deg":207.843}
     * clouds : {"all":68}
     * dt : 1570096173
     * sys : {"type":3,"id":20334,"message":0.0072,"country":"JP","sunrise":1570048770,"sunset":1570091212}
     * timezone : 32400
     * id : 1851632
     * name : Shuzenji
     * cod : 200
     */

    private CoordBean coord;
    private String base;
    private MainBean main;//최고 main.temp_max 최저/main.temp_main
    private WindBean wind;
    private CloudsBean clouds;
    private int dt;
    private SysBean sys;
    private int timezone;
    private int id;
    private String name;
    private int cod;
    private List<WeatherBean> weather; //날씨 icon

    public CoordBean getCoord() {
        return coord;
    }

    public String getBase() {
        return base;
    }

    public MainBean getMain() {
        return main;
    }

    public WindBean getWind() {
        return wind;
    }

    public CloudsBean getClouds() {
        return clouds;
    }

    public int getDt() {
        return dt;
    }

    public SysBean getSys() {
        return sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCod() {
        return cod;
    }

    public List<WeatherBean> getWeather() {
        return weather;
    }

    public static class CoordBean {
        /**
         * lon : 139
         * lat : 35
         */

        private double lon;
        private double lat;

        public double getLon() {
            return lon;
        }

        public double getLat() {
            return lat;
        }
    }

    public static class MainBean {
        /**
         * temp : 298.43
         * pressure : 1017
         * humidity : 70
         * temp_min : 298.15
         * temp_max : 299.26
         */

        private double temp;
        private int pressure;
        private int humidity;
        private double temp_min;
        private double temp_max;

        public double getTemp() {
            return temp;
        }

        public int getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public double getTemp_min() {
            return temp_min;
        }

        public double getTemp_max() {
            return temp_max;
        }
    }

    public static class WindBean {
        /**
         * speed : 2.75
         * deg : 207.843
         */

        private double speed;
        private double deg;

        public double getSpeed() {
            return speed;
        }

        public double getDeg() {
            return deg;
        }
    }

    public static class CloudsBean {
        /**
         * all : 68
         */

        private int all;

        public int getAll() {
            return all;
        }
    }

    public static class SysBean {
        /**
         * type : 3
         * id : 20334
         * message : 0.0072
         * country : JP
         * sunrise : 1570048770
         * sunset : 1570091212
         */

        private int type;
        private int id;
        private double message;
        private String country;
        private int sunrise;
        private int sunset;

        public int getType() {
            return type;
        }

        public int getId() {
            return id;
        }

        public double getMessage() {
            return message;
        }

        public String getCountry() {
            return country;
        }

        public int getSunrise() {
            return sunrise;
        }

        public int getSunset() {
            return sunset;
        }
    }

    public static class WeatherBean {
        /**
         * id : 803
         * main : Clouds
         * description : broken clouds
         * icon : 04n
         */

        private int id;
        private String main;
        private String description;
        private String icon;

        public int getId() {
            return id;
        }

        public String getMain() {
            return main;
        }

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }
}
