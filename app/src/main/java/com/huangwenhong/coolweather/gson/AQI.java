package com.huangwenhong.coolweather.gson;

/**
 * Created by huangwenhong on 2016/12/20.
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
