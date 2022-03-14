package com.example.weatherm.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastData {


    /**
     * cod : 200
     * message : 0
     * cnt : 40
     * list : [{"dt":1570892400,"main":{"temp":296.77,"temp_min":295.308,"temp_max":296.77,"pressure":984,"sea_level":984,"grnd_level":945.996,"humidity":77,"temp_kf":1.46},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}],"clouds":{"all":100},"wind":{"speed":3.292,"deg":285.611},"sys":{"pod":"n"},"dt_txt":"2019-10-12 15:00:00"},{"dt":1570903200,"main":{"temp":295.3,"temp_min":294.2,"temp_max":295.3,"pressure":993,"sea_level":993,"grnd_level":954.107,"humidity":75,"temp_kf":1.1},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04n"}],"clouds":{"all":67},"wind":{"speed":2.304,"deg":204.541},"sys":{"pod":"n"},"dt_txt":"2019-10-12 18:00:00"},{"dt":1570914000,"main":{"temp":295.03,"temp_min":294.299,"temp_max":295.03,"pressure":1000,"sea_level":1000,"grnd_level":960.889,"humidity":72,"temp_kf":0.73},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"clouds":{"all":0},"wind":{"speed":1.748,"deg":76.671},"sys":{"pod":"d"},"dt_txt":"2019-10-12 21:00:00"},{"dt":1570924800,"main":{"temp":298.5,"temp_min":298.139,"temp_max":298.5,"pressure":1005,"sea_level":1005,"grnd_level":966.968,"humidity":57,"temp_kf":0.37},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"clouds":{"all":0},"wind":{"speed":4.534,"deg":76.181},"sys":{"pod":"d"},"dt_txt":"2019-10-13 00:00:00"},{"dt":1570935600,"main":{"temp":299.3,"temp_min":299.3,"temp_max":299.3,"pressure":1009,"sea_level":1009,"grnd_level":970.052,"humidity":56,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"clouds":{"all":0},"wind":{"speed":4.758,"deg":90.132},"sys":{"pod":"d"},"dt_txt":"2019-10-13 03:00:00"},{"dt":1570946400,"main":{"temp":297.185,"temp_min":297.185,"temp_max":297.185,"pressure":1011,"sea_level":1011,"grnd_level":972.775,"humidity":51,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01d"}],"clouds":{"all":0},"wind":{"speed":7.27,"deg":67.237},"sys":{"pod":"d"},"dt_txt":"2019-10-13 06:00:00"},{"dt":1570957200,"main":{"temp":294.116,"temp_min":294.116,"temp_max":294.116,"pressure":1016,"sea_level":1016,"grnd_level":976.989,"humidity":69,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01n"}],"clouds":{"all":1},"wind":{"speed":6.427,"deg":87.788},"sys":{"pod":"n"},"dt_txt":"2019-10-13 09:00:00"},{"dt":1570968000,"main":{"temp":292.63,"temp_min":292.63,"temp_max":292.63,"pressure":1018,"sea_level":1018,"grnd_level":979.781,"humidity":68,"temp_kf":0},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"clouds":{"all":42},"wind":{"speed":7.029,"deg":89.959},"sys":{"pod":"n"},"dt_txt":"2019-10-13 12:00:00"},{"dt":1570978800,"main":{"temp":291.924,"temp_min":291.924,"temp_max":291.924,"pressure":1019,"sea_level":1019,"grnd_level":980.594,"humidity":76,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}],"clouds":{"all":85},"wind":{"speed":6.855,"deg":72.371},"sys":{"pod":"n"},"dt_txt":"2019-10-13 15:00:00"},{"dt":1570989600,"main":{"temp":290.862,"temp_min":290.862,"temp_max":290.862,"pressure":1020,"sea_level":1020,"grnd_level":980.487,"humidity":74,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":86},"wind":{"speed":6.429,"deg":59.322},"rain":{"3h":0.062},"sys":{"pod":"n"},"dt_txt":"2019-10-13 18:00:00"},{"dt":1571000400,"main":{"temp":290.004,"temp_min":290.004,"temp_max":290.004,"pressure":1020,"sea_level":1020,"grnd_level":981.243,"humidity":82,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":100},"wind":{"speed":8.05,"deg":66.275},"rain":{"3h":0.188},"sys":{"pod":"d"},"dt_txt":"2019-10-13 21:00:00"},{"dt":1571011200,"main":{"temp":288.077,"temp_min":288.077,"temp_max":288.077,"pressure":1021,"sea_level":1021,"grnd_level":982.433,"humidity":84,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":100},"wind":{"speed":5.673,"deg":49.998},"rain":{"3h":0.624},"sys":{"pod":"d"},"dt_txt":"2019-10-14 00:00:00"},{"dt":1571022000,"main":{"temp":289.072,"temp_min":289.072,"temp_max":289.072,"pressure":1019,"sea_level":1019,"grnd_level":980.125,"humidity":84,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":100},"wind":{"speed":5.351,"deg":51.198},"rain":{"3h":1},"sys":{"pod":"d"},"dt_txt":"2019-10-14 03:00:00"},{"dt":1571032800,"main":{"temp":288.98,"temp_min":288.98,"temp_max":288.98,"pressure":1018,"sea_level":1018,"grnd_level":979.246,"humidity":87,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":100},"wind":{"speed":5.793,"deg":58.785},"rain":{"3h":0.438},"sys":{"pod":"d"},"dt_txt":"2019-10-14 06:00:00"},{"dt":1571043600,"main":{"temp":289.046,"temp_min":289.046,"temp_max":289.046,"pressure":1018,"sea_level":1018,"grnd_level":979.151,"humidity":87,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":98},"wind":{"speed":3.933,"deg":56.597},"rain":{"3h":0.375},"sys":{"pod":"n"},"dt_txt":"2019-10-14 09:00:00"},{"dt":1571054400,"main":{"temp":289.501,"temp_min":289.501,"temp_max":289.501,"pressure":1018,"sea_level":1018,"grnd_level":978.789,"humidity":87,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":93},"wind":{"speed":2.885,"deg":67.675},"rain":{"3h":0.125},"sys":{"pod":"n"},"dt_txt":"2019-10-14 12:00:00"},{"dt":1571065200,"main":{"temp":289.334,"temp_min":289.334,"temp_max":289.334,"pressure":1017,"sea_level":1017,"grnd_level":978.016,"humidity":89,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":100},"wind":{"speed":2.536,"deg":76.358},"rain":{"3h":0.125},"sys":{"pod":"n"},"dt_txt":"2019-10-14 15:00:00"},{"dt":1571076000,"main":{"temp":289.179,"temp_min":289.179,"temp_max":289.179,"pressure":1017,"sea_level":1017,"grnd_level":977.629,"humidity":88,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":100},"wind":{"speed":2.362,"deg":87.015},"rain":{"3h":0.125},"sys":{"pod":"n"},"dt_txt":"2019-10-14 18:00:00"},{"dt":1571086800,"main":{"temp":288.729,"temp_min":288.729,"temp_max":288.729,"pressure":1019,"sea_level":1019,"grnd_level":979.605,"humidity":90,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":99},"wind":{"speed":5.054,"deg":64.206},"rain":{"3h":0.25},"sys":{"pod":"d"},"dt_txt":"2019-10-14 21:00:00"},{"dt":1571097600,"main":{"temp":288.292,"temp_min":288.292,"temp_max":288.292,"pressure":1021,"sea_level":1021,"grnd_level":981.718,"humidity":85,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":100},"wind":{"speed":5.047,"deg":64.912},"rain":{"3h":0.188},"sys":{"pod":"d"},"dt_txt":"2019-10-15 00:00:00"},{"dt":1571108400,"main":{"temp":290.3,"temp_min":290.3,"temp_max":290.3,"pressure":1020,"sea_level":1020,"grnd_level":981.347,"humidity":67,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":82},"wind":{"speed":5.302,"deg":63.401},"rain":{"3h":0.062},"sys":{"pod":"d"},"dt_txt":"2019-10-15 03:00:00"},{"dt":1571119200,"main":{"temp":289.28,"temp_min":289.28,"temp_max":289.28,"pressure":1020,"sea_level":1020,"grnd_level":981.664,"humidity":72,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],"clouds":{"all":85},"wind":{"speed":5.601,"deg":68.727},"sys":{"pod":"d"},"dt_txt":"2019-10-15 06:00:00"},{"dt":1571130000,"main":{"temp":288.982,"temp_min":288.982,"temp_max":288.982,"pressure":1022,"sea_level":1022,"grnd_level":982.888,"humidity":76,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":95},"wind":{"speed":4.955,"deg":69.133},"rain":{"3h":0.062},"sys":{"pod":"n"},"dt_txt":"2019-10-15 09:00:00"},{"dt":1571140800,"main":{"temp":288.939,"temp_min":288.939,"temp_max":288.939,"pressure":1023,"sea_level":1023,"grnd_level":983.907,"humidity":79,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":96},"wind":{"speed":4.7,"deg":66.845},"rain":{"3h":0.063},"sys":{"pod":"n"},"dt_txt":"2019-10-15 12:00:00"},{"dt":1571151600,"main":{"temp":288.193,"temp_min":288.193,"temp_max":288.193,"pressure":1023,"sea_level":1023,"grnd_level":983.782,"humidity":86,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":100},"wind":{"speed":4.685,"deg":57.1},"rain":{"3h":0.25},"sys":{"pod":"n"},"dt_txt":"2019-10-15 15:00:00"},{"dt":1571162400,"main":{"temp":287.393,"temp_min":287.393,"temp_max":287.393,"pressure":1023,"sea_level":1023,"grnd_level":983.744,"humidity":81,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":98},"wind":{"speed":4.553,"deg":49.909},"rain":{"3h":0.125},"sys":{"pod":"n"},"dt_txt":"2019-10-15 18:00:00"},{"dt":1571173200,"main":{"temp":286.7,"temp_min":286.7,"temp_max":286.7,"pressure":1024,"sea_level":1024,"grnd_level":984.266,"humidity":80,"temp_kf":0},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],"clouds":{"all":80},"wind":{"speed":4.575,"deg":52.647},"sys":{"pod":"d"},"dt_txt":"2019-10-15 21:00:00"},{"dt":1571184000,"main":{"temp":288.493,"temp_min":288.493,"temp_max":288.493,"pressure":1025,"sea_level":1025,"grnd_level":985.483,"humidity":72,"temp_kf":0},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],"clouds":{"all":77},"wind":{"speed":4.665,"deg":54.3},"sys":{"pod":"d"},"dt_txt":"2019-10-16 00:00:00"},{"dt":1571194800,"main":{"temp":289.739,"temp_min":289.739,"temp_max":289.739,"pressure":1023,"sea_level":1023,"grnd_level":984.266,"humidity":67,"temp_kf":0},"weather":[{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04d"}],"clouds":{"all":90},"wind":{"speed":3.965,"deg":72.685},"sys":{"pod":"d"},"dt_txt":"2019-10-16 03:00:00"},{"dt":1571205600,"main":{"temp":290.354,"temp_min":290.354,"temp_max":290.354,"pressure":1022,"sea_level":1022,"grnd_level":983.519,"humidity":63,"temp_kf":0},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04d"}],"clouds":{"all":64},"wind":{"speed":2.869,"deg":93.237},"sys":{"pod":"d"},"dt_txt":"2019-10-16 06:00:00"},{"dt":1571216400,"main":{"temp":287.163,"temp_min":287.163,"temp_max":287.163,"pressure":1024,"sea_level":1024,"grnd_level":984.34,"humidity":74,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01n"}],"clouds":{"all":0},"wind":{"speed":1.093,"deg":133.702},"sys":{"pod":"n"},"dt_txt":"2019-10-16 09:00:00"},{"dt":1571227200,"main":{"temp":286.564,"temp_min":286.564,"temp_max":286.564,"pressure":1026,"sea_level":1026,"grnd_level":985.893,"humidity":83,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01n"}],"clouds":{"all":2},"wind":{"speed":0.803,"deg":147.2},"sys":{"pod":"n"},"dt_txt":"2019-10-16 12:00:00"},{"dt":1571238000,"main":{"temp":287.6,"temp_min":287.6,"temp_max":287.6,"pressure":1026,"sea_level":1026,"grnd_level":985.716,"humidity":82,"temp_kf":0},"weather":[{"id":803,"main":"Clouds","description":"broken clouds","icon":"04n"}],"clouds":{"all":59},"wind":{"speed":0.654,"deg":131.59},"sys":{"pod":"n"},"dt_txt":"2019-10-16 15:00:00"},{"dt":1571248800,"main":{"temp":288.065,"temp_min":288.065,"temp_max":288.065,"pressure":1026,"sea_level":1026,"grnd_level":985.731,"humidity":84,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],"clouds":{"all":80},"wind":{"speed":1.652,"deg":72.091},"rain":{"3h":0.375},"sys":{"pod":"n"},"dt_txt":"2019-10-16 18:00:00"},{"dt":1571259600,"main":{"temp":287.578,"temp_min":287.578,"temp_max":287.578,"pressure":1026,"sea_level":1026,"grnd_level":986.331,"humidity":86,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":100},"wind":{"speed":1.841,"deg":66.164},"rain":{"3h":0.125},"sys":{"pod":"d"},"dt_txt":"2019-10-16 21:00:00"},{"dt":1571270400,"main":{"temp":289.026,"temp_min":289.026,"temp_max":289.026,"pressure":1027,"sea_level":1027,"grnd_level":987.518,"humidity":80,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":99},"wind":{"speed":3.167,"deg":70.907},"rain":{"3h":0.187},"sys":{"pod":"d"},"dt_txt":"2019-10-17 00:00:00"},{"dt":1571281200,"main":{"temp":290.878,"temp_min":290.878,"temp_max":290.878,"pressure":1026,"sea_level":1026,"grnd_level":986.663,"humidity":69,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":80},"wind":{"speed":3.031,"deg":80.544},"rain":{"3h":0.125},"sys":{"pod":"d"},"dt_txt":"2019-10-17 03:00:00"},{"dt":1571292000,"main":{"temp":290.911,"temp_min":290.911,"temp_max":290.911,"pressure":1025,"sea_level":1025,"grnd_level":985.968,"humidity":66,"temp_kf":0},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"clouds":{"all":44},"wind":{"speed":2.8,"deg":87.749},"rain":{"3h":0.063},"sys":{"pod":"d"},"dt_txt":"2019-10-17 06:00:00"},{"dt":1571302800,"main":{"temp":287.35,"temp_min":287.35,"temp_max":287.35,"pressure":1026,"sea_level":1026,"grnd_level":986.574,"humidity":81,"temp_kf":0},"weather":[{"id":800,"main":"Clear","description":"clear sky","icon":"01n"}],"clouds":{"all":0},"wind":{"speed":1.354,"deg":100.123},"sys":{"pod":"n"},"dt_txt":"2019-10-17 09:00:00"},{"dt":1571313600,"main":{"temp":288.456,"temp_min":288.456,"temp_max":288.456,"pressure":1027,"sea_level":1027,"grnd_level":987.361,"humidity":79,"temp_kf":0},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"clouds":{"all":33},"wind":{"speed":2.296,"deg":105.822},"sys":{"pod":"n"},"dt_txt":"2019-10-17 12:00:00"}]
     * city : {"id":1851632,"name":"Shuzenji","coord":{"lat":34.9667,"lon":138.9333},"country":"JP","timezone":32400,"sunrise":1570826808,"sunset":1570868086}
     */

    private String cod;
    private double message;
    private int cnt;
    private CityBean city;
    private List<ListBean> list;

    public String getCod() {
        return cod;
    }

    public double getMessage() {
        return message;
    }

    public int getCnt() {
        return cnt;
    }

    public CityBean getCity() {
        return city;
    }

    public List<ListBean> getList() {
        return list;
    }

    public static class CityBean {
        /**
         * id : 1851632
         * name : Shuzenji
         * coord : {"lat":34.9667,"lon":138.9333}
         * country : JP
         * timezone : 32400
         * sunrise : 1570826808
         * sunset : 1570868086
         */

        private int id;
        private String name;
        private CoordBean coord;
        private String country;
        private int timezone;
        private int sunrise;
        private int sunset;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public CoordBean getCoord() {
            return coord;
        }

        public String getCountry() {
            return country;
        }

        public int getTimezone() {
            return timezone;
        }

        public int getSunrise() {
            return sunrise;
        }

        public int getSunset() {
            return sunset;
        }

        public static class CoordBean {
            /**
             * lat : 34.9667
             * lon : 138.9333
             */

            private double lat;
            private double lon;

            public double getLat() {
                return lat;
            }

            public double getLon() {
                return lon;
            }
        }
    }

    public static class ListBean {
        /**
         * dt : 1570892400
         * main : {"temp":296.77,"temp_min":295.308,"temp_max":296.77,"pressure":984,"sea_level":984,"grnd_level":945.996,"humidity":77,"temp_kf":1.46}
         * weather : [{"id":804,"main":"Clouds","description":"overcast clouds","icon":"04n"}]
         * clouds : {"all":100}
         * wind : {"speed":3.292,"deg":285.611}
         * sys : {"pod":"n"}
         * dt_txt : 2019-10-12 15:00:00
         * rain : {"3h":0.062}
         */

        private long dt;
        private MainBean main;
        private CloudsBean clouds;
        private WindBean wind;
        private SysBean sys;
        private String dt_txt;
        private RainBean rain;
        private List<WeatherBean> weather;

        public long getDt() {
            return dt;
        }

        public MainBean getMain() {
            return main;
        }

        public CloudsBean getClouds() {
            return clouds;
        }

        public WindBean getWind() {
            return wind;
        }

        public SysBean getSys() {
            return sys;
        }

        public String getDt_txt() {
            return dt_txt;
        }

        public RainBean getRain() {
            return rain;
        }

        public List<WeatherBean> getWeather() {
            return weather;
        }


        public static class MainBean {
            /**
             * temp : 296.77
             * temp_min : 295.308
             * temp_max : 296.77
             * pressure : 984
             * sea_level : 984
             * grnd_level : 945.996
             * humidity : 77
             * temp_kf : 1.46
             */

            private double temp;
            private double temp_min;
            private double temp_max;
            private double pressure;
            private double sea_level;
            private double grnd_level;
            private int humidity;
            private double temp_kf;

            public double getTemp() {
                return temp;
            }

            public double getTemp_min() {
                return temp_min;
            }

            public double getTemp_max() {
                return temp_max;
            }

            public double getPressure() {
                return pressure;
            }

            public double getSea_level() {
                return sea_level;
            }

            public double getGrnd_level() {
                return grnd_level;
            }

            public int getHumidity() {
                return humidity;
            }

            public double getTemp_kf() {
                return temp_kf;
            }

        }

        public static class CloudsBean {
            /**
             * all : 100
             */

            private int all;

            public int getAll() {
                return all;
            }
        }

        public static class WindBean {
            /**
             * speed : 3.292
             * deg : 285.611
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

        public static class SysBean {
            /**
             * pod : n
             */

            private String pod;

            public String getPod() {
                return pod;
            }
        }

        public static class RainBean {
            /**
             * 3h : 0.062
             */

            @SerializedName("3h")
            private double _$3h;

            public double get_$3h() {
                return _$3h;
            }
        }

        public static class WeatherBean {
            /**
             * id : 804
             * main : Clouds
             * description : overcast clouds
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
}
