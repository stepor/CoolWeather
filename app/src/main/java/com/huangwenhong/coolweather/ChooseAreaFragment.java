package com.huangwenhong.coolweather;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huangwenhong.coolweather.db.City;
import com.huangwenhong.coolweather.db.Country;
import com.huangwenhong.coolweather.db.Province;
import com.huangwenhong.coolweather.util.HttpUtil;
import com.huangwenhong.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by huangwenhong on 2016/12/19.
 */

public class ChooseAreaFragment extends android.support.v4.app.Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;
    private static final String PROVINCE = "province";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private OnFragmentInteractionListener mListener;

    private Button backButton;
    private TextView titleTextView;
    private ListView listView;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;
    private List <String> dataList;

    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<Country> countryList;//县列表
    private Province selectedProvince;//选中的省
    private City selectedCity;//选中的市
    private int currentLevel;//当前选中级别

    public ChooseAreaFragment() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        backButton = (Button)view.findViewById(R.id.backButton);
        titleTextView = (TextView)view.findViewById(R.id.titleTextView);
        listView = (ListView)view.findViewById(R.id.listView);
        dataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if(currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCountries();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_COUNTRY) {
                    queryCities();
                } else if(currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    //查询全国所有省，优先从数据库查询，如果没有再去服务器上查询
    private void queryProvinces() {
        titleTextView.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0) {
            dataList.clear();
            for(Province p : provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, PROVINCE);
        }
    }

    //查询选中的省内所有的市，先从数据库中查询，如果没有从服务器中查询
    private void queryCities() {
        titleTextView.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).
                find(City.class);
        if(cityList.size() > 0) {
            dataList.clear();
            for(City c : cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode();
            queryFromServer(address, CITY);
        }
    }

    //查询选中的市内的所有的县，先从数据库中查询，如果没有从服务器中查询
    private void queryCountries() {
        titleTextView.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).
                find(Country.class);
        if(countryList.size() > 0) {
            dataList.clear();
            for(Country c : countryList) {
                dataList.add(c.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTRY;
        } else {
            String address = "http://guolin.tech/api/china" + selectedProvince.getProvinceCode() +
                    "/" + selectedCity.getCityCode();
            queryFromServer(address, COUNTRY);
        }
    }
    //从服务器中查询
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if(type.equals(PROVINCE)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if(type.equals(CITY)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if(type.equals(COUNTRY)) {
                    result = Utility.handleCountryResponse(responseText, selectedCity.getId());
                }
                if(result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(type.equals(PROVINCE)) {
                                queryProvinces();
                            } else if(type.equals(CITY)) {
                                queryCities();
                            } else if(type.equals(COUNTRY)) {
                                queryCountries();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
