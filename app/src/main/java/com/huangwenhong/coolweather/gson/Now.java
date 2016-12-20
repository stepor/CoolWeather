package com.huangwenhong.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huangwenhong on 2016/12/20.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}