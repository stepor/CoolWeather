package com.huangwenhong.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by huangwenhong on 2016/12/19.
 */

public class Country extends DataSupport {
    private int id;
    private String countryName;
    private String weatherId;
    private int cityId;

    public int getId() {
        return id;
    }
    public void setId(int _id) {
        id = id;
    }

    public String getCountryName() {
        return countryName;
    }
    public void setCountryName(String _countryName) {
        countryName = _countryName;
    }

    public String getWeatherId() {
        return weatherId;
    }
    public void setWeatherId(String _weatherId) {
        weatherId = _weatherId;
    }

    public int getCityId() {
        return cityId;
    }
    public void setCityId(int _cityId) {
        cityId = _cityId;
    }
}
