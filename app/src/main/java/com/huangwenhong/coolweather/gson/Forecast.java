package com.huangwenhong.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huangwenhong on 2016/12/20.
 */

public class Forecast {
    public String date;

    @SerializedName("hum")
    public String humidity;//相对湿度

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {
        public String max;
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
