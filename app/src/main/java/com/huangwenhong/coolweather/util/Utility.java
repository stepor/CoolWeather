package com.huangwenhong.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.Excluder;
import com.huangwenhong.coolweather.db.City;
import com.huangwenhong.coolweather.db.Country;
import com.huangwenhong.coolweather.db.Province;
import com.huangwenhong.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huangwenhong on 2016/12/19.
 */

public class Utility {

    //解析和处理服务器返回的省级数据
    private static final String TAG = "Utility";
    public static boolean handleProvinceResponse(String response) {
        if(!TextUtils.isEmpty(response)) {
            try{
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的市级数据
    public static  boolean handleCityResponse(String response, int provineId) {
        if(!TextUtils.isEmpty(response)) {
            try{
                JSONArray jsonArray = new JSONArray(response);
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(provineId);
                    city.save();
                }
                return true;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //解析和处理服务器返回的县级数据
    public static boolean handleCountryResponse(String response, int cityId) {
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Country country = new Country();
                    country.setCountryName(jsonObject.getString("name"));
                    country.setWeatherId(jsonObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //将返回的json数据解析成 weather 实体类
    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.i(TAG, "handleWeatherResponse: jsonObject:" + jsonObject.toString());
            Log.i(TAG, "handleWeatherResponse: -------------------------");
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            Log.i(TAG, "handleWeatherResponse: ================");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Log.i(TAG, "handleWeatherResponse:weatherContent: " + weatherContent);
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
