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
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.base.BaseMainFragment;
import com.hbmcc.shilinlin.networkoptz.database.LteBasestationCell;
import com.hbmcc.shilinlin.networkoptz.event.TabSelectedEvent;
import com.hbmcc.shilinlin.networkoptz.event.UpdateUeStatusEvent;
import com.hbmcc.shilinlin.networkoptz.telephony.LocationStatus;
import com.hbmcc.shilinlin.networkoptz.telephony.UeStatus;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.MainFragment;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.other.LteBasestationcellDetailInfoFragment;
import com.hbmcc.shilinlin.networkoptz.util.LocatonConverter;
import com.hbmcc.shilinlin.networkoptz.util.NumberFormat;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

import static android.content.Context.SENSOR_SERVICE;

public class SecondTabFragment extends BaseMainFragment implements SensorEventListener {
    public static final double DISTANCE_OFFSET = 0.01;
    public static final float MARKER_ALPHA = 0.7f;
    private static final String TAG = "SecondTabFragment";
    LteBasestationCell lteBasestationCell;
    // 是否首次定位
    private boolean isFirstLoc = true;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // UI相关
    private Button btnChangeMapType;
    private Button btnLocation;
    private CheckBox checkBoxTraffic;
    private CheckBox checkBoxBaiduHeatMap;
    private CheckBox checkboxFragmentSecondTabDisplayLTECell;
    private TextView textViewCurrentPositionLonLat;
    private TextView textViewCurrentPositionDefinition;
    private TextView textViewFragmentSecondTabClickedCell;
    private MyLocationConfiguration.LocationMode mCurrentLocationMode;
    private int mMapType;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private MyLocationData locData;
    private List<LteBasestationCell> lteBasestationCellList = new ArrayList<>();
    private List<LteBasestationCell> currentLteBasestationCellList = new ArrayList<>();
    private MarkerOptions markerOptions = new MarkerOptions();
    private Marker marker;
    private Marker lastSelectedMarker;
    private List<Marker> markerList = new ArrayList<>();

    // 主服务小区的连接线
    private Polyline mPolyline;

    //初始化marker信息
    // 初始化全局 bitmap 信息，不用时及时 recycle
    private BitmapDescriptor markerLteTDDOutside = BitmapDescriptorFactory
            .fromResource(R.drawable.roomout_4g_red);
    private BitmapDescriptor markerLteTDDOutsideSelected = BitmapDescriptorFactory
            .fromResource(R.drawable.roomout_4g_red_over);
    private BitmapDescriptor markerLteTDDIndoor = BitmapDescriptorFactory
            .fromResource(R.drawable.roomin_4g);
    private BitmapDescriptor markerLteTDDIndoorSelected = BitmapDescriptorFactory
            .fromResource(R.drawable.roomin_4g_over);
    private BitmapDescriptor markerLteFDD900Outside = BitmapDescriptorFactory
            .fromResource(R.drawable.roomout_4g_green);
    private BitmapDescriptor markerLteFDD900OutsideSelected = BitmapDescriptorFactory
            .fromResource(R.drawable.roomout_4g_green_over);
    private BitmapDescriptor markerLteFDD1800Outside = BitmapDescriptorFactory
            .fromResource(R.drawable.roomout_4g_yellow);
    private BitmapDescriptor markerLteFDD1800OutsideSelected = BitmapDescriptorFactory
            .fromResource(R.drawable.roomout_4g_yellow_over);

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
        if (event.position != MainFragment.SECOND) {
            return;
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
        mBaiduMap.setMyLocationEnabled(false);
        markerLteTDDOutside.recycle();
        markerLteFDD900Outside.recycle();
        markerLteFDD1800Outside.recycle();
        markerLteTDDIndoor.recycle();
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
        textViewFragmentSecondTabClickedCell = view.findViewById(R.id.textView_fragment_second_tab_clicked_cell);
        btnChangeMapType = view.findViewById(R.id.btn_fragment_second_tab_change_map_type);
        btnLocation = view.findViewById(R.id.btn_fragment_second_tab_location);
        checkBoxTraffic = view.findViewById(R.id.checkBox_fragment_second_tab_traffic);
        checkBoxBaiduHeatMap = view.findViewById(R.id.checkbox_fragment_second_tab_baidu_heat_map);
        checkboxFragmentSecondTabDisplayLTECell = view.findViewById(R.id
                .checkbox_fragment_second_tab_LTE_cell);

        //获取传感器管理服务
        mSensorManager = (SensorManager) _mActivity.getSystemService(SENSOR_SERVICE);

        // 地图初始化
        mMapView = view.findViewById(R.id.bmapView_fragment_second_tab_bmapview);
        mBaiduMap = mMapView.getMap();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initMap();
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
                    default:
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

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                final LatLng ll = mapStatus.target;
                if (checkboxFragmentSecondTabDisplayLTECell.isChecked()) {
                    clearOverlay();
                    displayMyOverlay(ll);
                } else {
                    clearOverlay();
                }

                checkboxFragmentSecondTabDisplayLTECell.setOnCheckedChangeListener(new CheckBox
                        .OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            clearOverlay();
                            displayMyOverlay(ll);
                        } else {
                            clearOverlay();
                        }
                    }
                });
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                textViewFragmentSecondTabClickedCell.setText(lteBasestationCellList.get(marker
                        .getZIndex())
                        .getName());

//                textViewFragmentSecondTabClickedCell.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        extraTransaction()
//                                .startDontHideSelf
//                                (LteBasestationcellDetailInfoFragment
//                                .newInstance
//                                        (lteBasestationCellList.get(marker
//                                                .getZIndex())));
//                    }
//                });

                if (lastSelectedMarker != null) {
                    if (lastSelectedMarker.getIcon() == markerLteFDD900OutsideSelected) {
                        lastSelectedMarker.setIcon(markerLteFDD900Outside);

                    } else if (lastSelectedMarker.getIcon() == markerLteFDD1800OutsideSelected) {
                        lastSelectedMarker.setIcon(markerLteFDD1800Outside);

                    } else if (lastSelectedMarker.getIcon() == markerLteTDDOutsideSelected) {
                        lastSelectedMarker.setIcon(markerLteTDDOutside);

                    } else if (lastSelectedMarker.getIcon() == markerLteTDDIndoorSelected) {
                        lastSelectedMarker.setIcon(markerLteTDDIndoor);

                    }
                }
                marker.setToTop();
                if (marker.getIcon() == markerLteFDD900Outside) {
                    marker.setIcon(markerLteFDD900OutsideSelected);

                } else if (marker.getIcon() == markerLteFDD1800Outside) {
                    marker.setIcon(markerLteFDD1800OutsideSelected);

                } else if (marker.getIcon() == markerLteTDDOutside) {
                    marker.setIcon(markerLteTDDOutsideSelected);

                } else if (marker.getIcon() == markerLteTDDIndoor) {
                    marker.setIcon(markerLteTDDIndoorSelected);

                }
                lastSelectedMarker = marker;
                return true;
            }
        });

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

        //设置初步地图类型为二维地图（二维、卫星）
        mMapType = BaiduMap.MAP_TYPE_NORMAL;
        btnChangeMapType.setText("二维");

        //设置地图各组件的位置
        mBaiduMap.setViewPadding(0, 0, 0, 0);

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
    public void updateLocation(UpdateUeStatusEvent updateUEStatusEvent) {
        if (SecondTabFragment.this.isVisible()) {
            getCurrentLocation(updateUEStatusEvent.ueStatus.locationStatus);
            //在界面上更新当前地点的经纬度、名称等信息

            //更新地图
            if (updateUEStatusEvent.ueStatus.locationStatus.bdLocation == null || mMapView == null) {
                return;
            }
            mCurrentLat = updateUEStatusEvent.ueStatus.locationStatus.latitudeBaidu;
            mCurrentLon = updateUEStatusEvent.ueStatus.locationStatus.longitudeBaidu;
            mCurrentAccracy = updateUEStatusEvent.ueStatus.locationStatus.radius;
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
            displayCellLine(updateUEStatusEvent.ueStatus);
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

    /**
     * 清除所有Overlay
     *
     * @param
     */
    public void clearOverlay() {
        mBaiduMap.clear();
        markerList.clear();
    }

    public void displayMyOverlay(LatLng latLngBd09) {
        LocatonConverter.MyLatLng myLatLngWgs84 = LocatonConverter.bd09ToWgs84(new LocatonConverter
                .MyLatLng(latLngBd09.latitude, latLngBd09.longitude));
        lteBasestationCellList = LitePal.where("lng<? and " +
                "lng>? and lat<? and lat >?", myLatLngWgs84.longitude + DISTANCE_OFFSET
                + "", myLatLngWgs84
                .longitude - DISTANCE_OFFSET + "", myLatLngWgs84.latitude + DISTANCE_OFFSET + "", myLatLngWgs84
                .latitude - DISTANCE_OFFSET + "").find(LteBasestationCell.class);
        // add marker overlay
        if (lteBasestationCellList.size() > 0) {
            for (int i = 0; i < lteBasestationCellList.size(); i++) {
                lteBasestationCell = lteBasestationCellList.get(i);
                //这里需要将数据库中查出来的每个站点均进行wgs84 to bd09的变换
                LocatonConverter.MyLatLng myLatLngBd09 = LocatonConverter.wgs84ToBd09(new
                        LocatonConverter.MyLatLng(lteBasestationCell
                        .getLat(), lteBasestationCell
                        .getLng()));
                LatLng ll = new LatLng(myLatLngBd09.getLatitude(), myLatLngBd09.getLongitude());
                if (lteBasestationCell.getIndoorOrOutdoor() == LteBasestationCell.COVERAGE_OUTSIDE) {
                    if (lteBasestationCell.getLteEarFcn() > 10000) {
                        markerOptions = new MarkerOptions().position(ll).icon(markerLteTDDOutside)
                                .zIndex(i)
                                .draggable(false).alpha(MARKER_ALPHA).rotate(360 -
                                        lteBasestationCell.getEnbCellAzimuth()).perspective(true);
                    } else if (lteBasestationCell.getLteEarFcn() > 3000 && lteBasestationCell
                            .getLteEarFcn() <= 10000) {
                        markerOptions = new MarkerOptions().position(ll).icon
                                (markerLteFDD900Outside)
                                .zIndex(i)
                                .draggable(false).alpha(MARKER_ALPHA).rotate(360 -
                                        lteBasestationCell.getEnbCellAzimuth()).perspective(true);
                    } else if (lteBasestationCell.getLteEarFcn() < 3000) {
                        markerOptions = new MarkerOptions().position(ll).icon
                                (markerLteFDD1800Outside)
                                .zIndex(i)
                                .draggable(false).alpha(MARKER_ALPHA).rotate(360 -
                                        lteBasestationCell.getEnbCellAzimuth()).perspective(true);
                    }
                } else if (lteBasestationCell.getIndoorOrOutdoor() == LteBasestationCell.COVERAGE_INDOOR) {
                    markerOptions = new MarkerOptions().position(ll).icon
                            (markerLteTDDIndoor)
                            .zIndex(i)
                            .draggable(false).alpha(MARKER_ALPHA).perspective(true);
                }
                marker = (Marker) (mBaiduMap.addOverlay(markerOptions));
                markerList.add(marker);
            }
        }
    }

    private void displayCellLine(UeStatus ueStatus) {
        if (checkboxFragmentSecondTabDisplayLTECell.isChecked()) {
            currentLteBasestationCellList = LitePal.where("eci=?", ueStatus.networkStatus
                    .lteServingCellTower
                    .cellId + "").find
                    (LteBasestationCell.class);
            if (lteBasestationCellList.size() > 0) {
                LatLng p1 = new LatLng(ueStatus.locationStatus.latitudeBaidu, ueStatus.locationStatus.longitudeBaidu);
                LocatonConverter.MyLatLng myLatLngBd09 = LocatonConverter.wgs84ToBd09(new
                        LocatonConverter
                                .MyLatLng
                        (currentLteBasestationCellList.get(0).getLat(), currentLteBasestationCellList
                                .get(0).getLng()));
                LatLng p2 = new LatLng(myLatLngBd09.latitude, myLatLngBd09.longitude);
                List<LatLng> points = new ArrayList<>();
                points.add(p1);
                points.add(p2);
                OverlayOptions ooPolyline = new PolylineOptions().width(1).color(0xAAFF0000).points
                        (points);
                mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
            }
        }
    }
}
