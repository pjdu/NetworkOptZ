package com.hbmcc.shilinlin.networkoptz;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hbmcc.shilinlin.networkoptz.event.UpdateUEStatusEvent;
import com.hbmcc.shilinlin.networkoptz.telephony.DownloadSpeedStatus;
import com.hbmcc.shilinlin.networkoptz.telephony.LocationStatus;
import com.hbmcc.shilinlin.networkoptz.telephony.NetworkStatus;
import com.hbmcc.shilinlin.networkoptz.telephony.UEStatus;
import com.hbmcc.shilinlin.networkoptz.telephony.UploadSpeedStatus;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.MainFragment;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;


public class MainActivity extends SupportActivity {
    private static final String TAG = "MainActivity";
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private UEStatus ueStatus;
    private NetworkStatus networkStatus;
    private UploadSpeedStatus uploadSpeedStatus;
    private DownloadSpeedStatus downloadSpeedStatus;
    private LocationStatus locationStatus;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }

        networkStatus = new NetworkStatus();
        uploadSpeedStatus = new UploadSpeedStatus();
        downloadSpeedStatus = new DownloadSpeedStatus();
        locationStatus = new LocationStatus();

        // 定位初始化
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        requestLocation();

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置纵向(和安卓4.x动画相同)
        return new DefaultVerticalAnimator();
//        设置横向
//        return new DefaultHorizontalAnimator();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "必须同意所有权限", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "发生未知错误", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
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

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location!=null) {
                networkStatus.updateStatus();
                locationStatus.updateStatus(location);
                ueStatus = new UEStatus(networkStatus,locationStatus,downloadSpeedStatus,uploadSpeedStatus);
//                Log.d(TAG, "onReceiveLocation: "+ueStatus.locationStatus.longitudeBaidu);
//                Log.d(TAG, "onReceiveLocation: "+ueStatus.networkStatus.lteServingCellTower.tac);
                UpdateUEStatusEvent updateUEStatusEvent =new UpdateUEStatusEvent(ueStatus);
                EventBusActivityScope.getDefault(MainActivity.this).post(updateUEStatusEvent);

            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
