package com.hbmcc.shilinlin.networkoptz.ui.fragment.second;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.event.UpdateLocationStatusEvent;
import com.hbmcc.shilinlin.networkoptz.telephony.LocationStatus;
import com.hbmcc.shilinlin.networkoptz.base.BaseMainFragment;
import com.hbmcc.shilinlin.networkoptz.event.TabSelectedEvent;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.MainFragment;
import com.hbmcc.shilinlin.networkoptz.util.NumberFormat;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

import static android.content.Context.SENSOR_SERVICE;

public class SecondTabFragment extends BaseMainFragment implements SensorEventListener {

    public static final double DISTANCE_OFFSET = 0.01;
    public static final float MARKER_ALPHA = 0.7f;
    private boolean isFirstLoc = true; // 是否首次定位
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // UI相关
    private Button btnChangeMapType;
    private Button btnLocation;
    private CheckBox checkBoxTraffic;
    private CheckBox checkBoxBaiduHeatMap;

    private TextView textViewCurrentPositionLonLat;
    private TextView textViewCurrentPositionDefinition;
    private MyLocationConfiguration.LocationMode mCurrentLocationMode;
    private int mMapType;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private MyLocationData locData;


    public static SecondTabFragment newInstance() {
        Bundle args = new Bundle();
        SecondTabFragment fragment = new SecondTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_tab, container,
                false);
        initView(view);
        return view;
    }

    /**
     * Reselected Tab
     */
    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {
        if (event.position != MainFragment.SECOND) return;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    public void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    private void initView(View view) {

        EventBusActivityScope.getDefault(_mActivity).register(this);

        textViewCurrentPositionLonLat = view.findViewById(R.id.textView_fragment_second_tab_current_position_lon_lat);
        textViewCurrentPositionDefinition = view.findViewById(R.id
                .textView_fragment_second_tab_current_position_definition);
        btnChangeMapType = view.findViewById(R.id.btn_fragment_second_tab_change_map_type);
        btnLocation = view.findViewById(R.id.btn_fragment_second_tab_location);
        checkBoxTraffic = view.findViewById(R.id.checkBox_fragment_second_tab_traffic);
        checkBoxBaiduHeatMap = view.findViewById(R.id.checkbox_fragment_second_tab_baidu_heat_map);
        mSensorManager = (SensorManager) _mActivity.getSystemService(SENSOR_SERVICE);//获取传感器管理服务


        // 地图初始化
        mMapView = view.findViewById(R.id.bmapView_fragment_second_tab_bmapview);
        mBaiduMap = mMapView.getMap();
        initMap();

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
                                                   @Override
                                                   public void onMapStatusChangeStart(MapStatus mapStatus) {
                                                   }

                                                   @Override
                                                   public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
                                                   }

                                                   @Override
                                                   public void onMapStatusChange(MapStatus mapStatus) {
                                                   }

                                                   @Override
                                                   public void onMapStatusChangeFinish(MapStatus mapStatus) {

                                                   }
                                               }
        );


        btnChangeMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mMapType) {
                    case BaiduMap.MAP_TYPE_NORMAL:
                        btnChangeMapType.setText("卫星");
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        mMapType = BaiduMap.MAP_TYPE_SATELLITE;
                        break;
                    case BaiduMap.MAP_TYPE_SATELLITE:
                        btnChangeMapType.setText("二维");
                        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        mMapType = BaiduMap.MAP_TYPE_NORMAL;
                        break;
                }
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCurrentLocation();
            }
        });

        checkBoxBaiduHeatMap.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBaiduMap.setBaiduHeatMapEnabled(isChecked);
            }
        });

        checkBoxTraffic.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBaiduMap.setTrafficEnabled(isChecked);
            }
        });
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    private void initMap() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //显示方向，初始化为普通地图模式（普通、跟随、导航）
        mCurrentLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentLocationMode, true, null));
        final MapStatus.Builder builder = new MapStatus.Builder();
        builder.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        //显示指南针
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        //实例化UiSettings类对象

        mUiSettings.setCompassEnabled(true);

//        mBaiduMap.setCompassPosition(new Point((int) mMapView.getX() + 80, (int) mMapView.getY() + 80));

        //设置初步地图类型为二维地图（二维、卫星）
        mMapType = BaiduMap.MAP_TYPE_NORMAL;
        btnChangeMapType.setText("二维");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateLocation(UpdateLocationStatusEvent updateLocationStatusEvent) {

        getCurrentLocation(updateLocationStatusEvent.locationStatus);
        //在界面上更新当前地点的经纬度、名称等信息

        //更新地图
        if (updateLocationStatusEvent.locationStatus.bdLocation == null || mMapView == null) {
            return;
        }

        mCurrentLat = updateLocationStatusEvent.locationStatus.latitudeBaidu;
        mCurrentLon = updateLocationStatusEvent.locationStatus.longitudeBaidu;
        mCurrentAccracy = updateLocationStatusEvent.locationStatus.radius;
        locData = new MyLocationData.Builder()
                .accuracy(mCurrentAccracy)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection).latitude(mCurrentLat)
                .longitude(mCurrentLon).build();
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            goToCurrentLocation();
        }

    }


    private void getCurrentLocation(LocationStatus locationStatus) {
        StringBuilder currentPostion = new StringBuilder();
                currentPostion.append("经纬度:").append(NumberFormat.doubleFormat(locationStatus
                .longitudeWgs84, 6))
                .append(",")
                .append
                        (NumberFormat.doubleFormat(locationStatus.latitudeWgs84, 6)).append("," +
                "高度:").append
                (locationStatus.altitude);

        textViewCurrentPositionLonLat.setText(currentPostion);
        textViewCurrentPositionDefinition.setText("详细地址" + locationStatus.addrStr);
    }




    private void goToCurrentLocation() {
        LatLng ll = new LatLng(mCurrentLat,
                mCurrentLon);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }
}
