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

    //百度地图LocationClient和回调Listener
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    //获取UEStatus并通过UpdateUEStatusEvent进行广播
    private UEStatus ueStatus;
    private NetworkStatus networkStatus;
    private UploadSpeedStatus uploadSpeedStatus;
    private DownloadSpeedStatus downloadSpeedStatus;
    private LocationStatus locationStatus;

    //只有Event中前后两条记录的time不一致，才进行广播
    private String mTime;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTime = "";
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }

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
        option.setScanSpan(3000);//设置发起定位请求的间隔，int类型，单位ms。如果设置为0，则代表单次定位，即仅定位一次，默认为0。如果设置非0
        // ，需设置1000ms以上才有效
        option.setCoorType("bd09ll");//设置返回经纬度坐标类型，默认gcj02。gcj02：国测局坐标；/bd09ll：百度经纬度坐标；bd09：百度墨卡托坐标；海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标
        option.setOpenGps(true);//设置是否打开gps进行定位
        option.setWifiCacheTimeOut(5 * 60 * 1000);//7.2版本新增能力，设置wifi缓存超时时间阈值，超过该阈值，首次定位将会主动扫描wifi以使得定位精准度提高，定位速度会有所下降，具体延时取决于wifi扫描时间，大约是1-3秒
        option.setIsNeedAltitude(true);//获取高度信息，目前只有是GPS定位结果时或者设置LocationClientOption.setIsNeedAltitude(true)时才有效，单位米
        option.setIsNeedAddress(true);//设置是否需要地址信息，默认为无地址
        option.setIsNeedLocationDescribe(true);//设置是否需要返回位置语义化信息，可以在BDLocation
        option.setNeedDeviceDirect(true);//在网络定位时，是否需要设备方向 true:需要 ; false:不需要。
        option.setIsNeedLocationDescribe(true);// getLocationDescribe()中得到数据，ex:"在天安门附近"， 可以用作地址信息的补充
        option.setEnableSimulateGps(true);//设置是否允许仿真GPS信号
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location) {

            if (location != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //收到百度定位的回调后，开始获取NetworkStatus并在EventBus中广播
                        networkStatus = new NetworkStatus();

                        //只有Event中前后两条记录的time不一致，才进行广播
                        if(!mTime.equals(networkStatus.time)){
                            uploadSpeedStatus = new UploadSpeedStatus();
                            downloadSpeedStatus = new DownloadSpeedStatus();
                            locationStatus = new LocationStatus(location);
                            ueStatus = new UEStatus(networkStatus, locationStatus, downloadSpeedStatus, uploadSpeedStatus);
                            UpdateUEStatusEvent updateUEStatusEvent = new UpdateUEStatusEvent(ueStatus);
                            EventBusActivityScope.getDefault(MainActivity.this).post(updateUEStatusEvent);
                            mTime = networkStatus.time;
                        }
                    }
                }).start();


            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
