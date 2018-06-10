package com.hbmcc.shilinlin.networkoptz.ui.fragment.second;

import android.graphics.Point;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.Util.LocatonConverter;
import com.hbmcc.shilinlin.networkoptz.base.BaseMainFragment;
import com.hbmcc.shilinlin.networkoptz.event.TabSelectedEvent;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.MainFragment;

import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

import static android.content.Context.SENSOR_SERVICE;

public class SecondTabFragment extends BaseMainFragment implements SensorEventListener {

    public static final double DISTANCE_OFFSET = 0.01;
    public static final float MARKER_ALPHA = 0.7f;
    private LocationClient mLocationClient = null;
    private boolean isFirstLoc = true; // 是否首次定位
    private BitmapDescriptor mCurrentMarker;
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    // UI相关
    private Button btnChangeLocationMode;
    private Button btnChangeMapType;
    private MyLocationListener myListener = new MyLocationListener();
    private TextView textViewCurrentPositionLonLat;
    private TextView textViewCurrentMarkerBaseStationName;
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
    //    private List<LteDatabase> lteDatabaseList = new ArrayList<>();
    private List<LatLng> llList = new ArrayList<>();
    private List<MarkerOptions> markerOptionList = new ArrayList<>();
    //    private LteDatabase lteDatabase = new LteDatabase();
    private MarkerOptions markerOptions = new MarkerOptions();
    private Marker marker;
    private Marker lastMarker;
    private List<Marker> markerList = new ArrayList<>();
    private CheckBox checkBoxDisplayLteDatabase;

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

    private void initView(View view) {

        EventBusActivityScope.getDefault(_mActivity).register(this);

        textViewCurrentPositionLonLat = view.findViewById(R.id.TextViewCurrentPostionLonLat);
        textViewCurrentPositionDefinition = view.findViewById(R.id
                .TextViewCurrentPostionDefinition);
        textViewCurrentMarkerBaseStationName = view.findViewById(R.id
                .TextViewCurrentMarkerBaseStationName);
        btnChangeLocationMode = view.findViewById(R.id.BtnChangeLocationMode);
        btnChangeMapType = view.findViewById(R.id.BtnChangeMapType);
        mSensorManager = (SensorManager) _mActivity.getSystemService(SENSOR_SERVICE);//获取传感器管理服务

        // 定位初始化
        mLocationClient = new LocationClient(_mActivity);
        mLocationClient.registerLocationListener(myListener);
        requestLocation();

        // 地图初始化
        mMapView = view.findViewById(R.id.bmapView);
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
                LatLng latLng = mapStatus.target;
                LocatonConverter.LatLng latLngll = new LocatonConverter.LatLng(latLng.latitude,
                        latLng.longitude);
                latLngll = LocatonConverter.bd09ToWgs84(latLngll);
                final LatLng ll = new LatLng(latLngll.getLatitude(), latLngll.getLongitude());

                checkBoxDisplayLteDatabase = (CheckBox) view.findViewById(R.id
                        .checkboxDisplayLteDatabase);
                if (checkBoxDisplayLteDatabase.isChecked()) {
                    clearOverlay(null);
                    disPlayMyOverlay(ll);
                } else {
                    clearOverlay(null);
                }
                checkBoxDisplayLteDatabase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (checkBoxDisplayLteDatabase.isChecked()) {
                            clearOverlay(null);
                            disPlayMyOverlay(ll);
                        } else {
                            clearOverlay(null);
                        }
                    }
                });

            }
        });

        btnChangeLocationMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentLocationMode) {
                    case NORMAL:
                        btnChangeLocationMode.setText("跟随");
                        mCurrentLocationMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentLocationMode, true, mCurrentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0).rotate(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        btnChangeLocationMode.setText("普通");
                        mCurrentLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentLocationMode, true, mCurrentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0).rotate(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        btnChangeLocationMode.setText("罗盘");
                        mCurrentLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentLocationMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
            }
        });

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

//        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//            public boolean onMarkerClick(final Marker marker) {
//                textViewCurrentMarkerBaseStationName.setText(lteDatabaseList.get(marker
//                        .getZIndex())
//                        .getEnbCellName());
//                if(lastMarker!=null){
//                    lastMarker.setAlpha(MARKER_ALPHA);
//                }
//                marker.setToTop();
//                marker.setAlpha(1.0f);
//                lastMarker = marker;
//                return true;
//            }
//        });

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
        btnChangeLocationMode.setText("普通");
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentLocationMode, true, mCurrentMarker));
        final MapStatus.Builder builder = new MapStatus.Builder();
        builder.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        //显示指南针
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        //实例化UiSettings类对象

        mUiSettings.setCompassEnabled(true);

        mBaiduMap.setCompassPosition(new Point((int) mMapView.getX() + 80, (int) mMapView.getY() + 80));

        //设置初步地图类型为二维地图（二维、卫星）
        mMapType = BaiduMap.MAP_TYPE_NORMAL;
        btnChangeMapType.setText("二维");
    }


    /**
     * 清除所有Overlay
     *
     * @param view
     */
    public void clearOverlay(View view) {
        mBaiduMap.clear();
        markerList.clear();
        textViewCurrentMarkerBaseStationName.setText("当前未点击小区");
    }


    public void disPlayMyOverlay(LatLng latLng) {
//        lteDatabaseList = DataSupport.where("enbCellLng<? and " +
//                "enbCellLng>? and enbCellLat<? and enbCellLat >?", latLng.longitude + DISTANCE_OFFSET + "", latLng
//                .longitude - DISTANCE_OFFSET + "", latLng.latitude + DISTANCE_OFFSET + "", latLng
//                .latitude - DISTANCE_OFFSET + "").find(LteDatabase.class);
//        // add marker overlay
//        if (lteDatabaseList.size() > 0) {
//            for (int i = 0; i < lteDatabaseList.size(); i++) {
//                lteDatabase = lteDatabaseList.get(i);
//                //这里需要添加wgs84 to bd09的变换
//                LocatonConverter.LatLng latLngll = new LocatonConverter.LatLng(lteDatabase
//                        .getEnbCellLat(), lteDatabase
//                        .getEnbCellLng());
//                latLngll = LocatonConverter.wgs84ToBd09(latLngll);
//                LatLng ll = new LatLng(latLngll.getLatitude(), latLngll.getLongitude());
//                llList.add(ll);
//                if (lteDatabase.getEnbCellCoverageType() == LteDatabase.COVERAGE_OUTSIDE) {
//                    if (lteDatabase.getLteEarFcn() > 10000) {
//                        markerOptions = new MarkerOptions().position(ll).icon(markerLteTDDOutside)
//                                .zIndex(i)
//                                .draggable(false).alpha(MARKER_ALPHA).rotate(360 - lteDatabase.getEnbCellAzimuth());
//                        markerOptionList.add(markerOptions);
//                    } else {
//                        markerOptions = new MarkerOptions().position(ll).icon(markerLteFDDOutside)
//                                .zIndex(i)
//                                .draggable(false).alpha(MARKER_ALPHA).rotate(360 - lteDatabase.getEnbCellAzimuth());
//                        markerOptionList.add(markerOptions);
//                    }
//                } else if (lteDatabase.getEnbCellCoverageType() == LteDatabase.COVERAGE_INDOOR) {
//                    if (lteDatabase.getLteEarFcn() > 10000) {
//                        markerOptions = new MarkerOptions().position(ll).icon
//                                (markerLteTDDIndoor)
//                                .zIndex(i)
//                                .draggable(false).alpha(MARKER_ALPHA);
//                        markerOptionList.add(markerOptions);
//                    } else {
//                        markerOptions = new MarkerOptions().position(ll).icon
//                                (markerLteFDDIndoor)
//                                .zIndex(i)
//                                .draggable(false).alpha(MARKER_ALPHA);
//                        markerOptionList.add(markerOptions);
//                    }
//                }
//                marker = (Marker) (mBaiduMap.addOverlay(markerOptions));
//                markerList.add(marker);
//            }
//        }

    }

    /**
     * 设置是否显示交通图
     *
     * @param view
     */
    public void setTraffic(View view) {
        mBaiduMap.setTrafficEnabled(((CheckBox) view).isChecked());
    }

    /**
     * 设置是否显示百度热力图
     *
     * @param view
     */
    public void setBaiduHeatMap(View view) {
        mBaiduMap.setBaiduHeatMapEnabled(((CheckBox) view).isChecked());
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

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);//设置发起定位请求的间隔，int类型，单位ms。如果设置为0，则代表单次定位，即仅定位一次，默认为0。如果设置非0，需设置1000ms以上才有效
        option.setCoorType("bd09ll");//设置返回经纬度坐标类型，默认gcj02。gcj02：国测局坐标；/bd09ll：百度经纬度坐标；bd09：百度墨卡托坐标；海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
        option.setOpenGps(true);//设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setWifiCacheTimeOut(5 * 60 * 1000);//7.2版本新增能力，如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位
        option.setIsNeedAltitude(true);//获取高度信息，目前只有是GPS定位结果时或者设置LocationClientOption.setIsNeedAltitude(true)时才有效，单位米
        option.setIsNeedAddress(true);//设置是否需要地址信息，默认为无地址
        mLocationClient.setLocOption(option);
    }

    private void getCurrentLocation(BDLocation location) {
        StringBuilder currentPostion = new StringBuilder();
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取经纬度相关（常用）的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        double latitudeBaidu = location.getLatitude();    //获取纬度信息
        double longitudeBaidu = location.getLongitude();    //获取经度信息
        LocatonConverter.LatLng latLngBaidu = new LocatonConverter.LatLng(latitudeBaidu,
                longitudeBaidu);
        LocatonConverter.LatLng latLngWgs84 = LocatonConverter.bd09ToWgs84(latLngBaidu);
        double latitudeWgs84 = latLngWgs84.getLatitude();
        double longitudeWgs84 = latLngWgs84.getLongitude();
        float radius = location.getRadius();    //获取定位精度，默认值为0.0f
        double altitude = location.getAltitude();

        String coorType = location.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

        int errorCode = location.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

        String networkLocationCode = location.getNetworkLocationType();//在网络定位结果的情况下，获取网络定位结果是通过基站定位得到的还是通过wifi定位得到的还是GPS得结果
        // 返回: String : "wf"： wifi定位结果 “cl“； cell定位结果 “ll”：GPS定位结果 null 没有获取到定位结果采用的类型

        String province = location.getProvince();//省
        String city = location.getCity();//市
        String district = location.getDistrict();//区县
        String street = location.getStreet();//街道
        String streetNumber = location.getStreetNumber();//街道号码
        String addrStr = location.getAddrStr();//详细地址

        currentPostion.append("经纬度:").append(doubleFormat(longitudeWgs84, 6)).append(",")
                .append
                        (doubleFormat(latitudeWgs84, 6)).append(",高度:").append(altitude);

        textViewCurrentPositionLonLat.setText(currentPostion);
        textViewCurrentPositionDefinition.setText("详细地址" + addrStr);
    }

    public double doubleFormat(double x, int decimalPlaces) {
        BigDecimal b = new BigDecimal(x);//BigDecimal 类使用户能完全控制舍入行为
        double y = b.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP).doubleValue();
        return y;
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

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            getCurrentLocation(location);//在界面上更新当前地点的经纬度、名称等信息

            //更新地图
            if (location == null || mMapView == null) {
                return;
            }

            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(mCurrentLat,
                        mCurrentLon);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }

    }
}
