package com.hbmcc.shilinlin.networkoptz.telephony;

import com.baidu.location.BDLocation;
import com.hbmcc.shilinlin.networkoptz.util.LocatonConverter;

public class LocationStatus {
    public BDLocation bdLocation;
    public double latitudeBaidu, longitudeBaidu, latitudeWgs84, longitudeWgs84, altitude;
    public float radius;
    public String coorType, networkLocationCode, province, city, district, street, streetNumber,
            addrStr;
    public int errorCode;

    public LocationStatus(BDLocation bdLocation) {
        this.bdLocation = bdLocation;
        latitudeBaidu = bdLocation.getLatitude();    //获取纬度信息
        longitudeBaidu = bdLocation.getLongitude();    //获取经度信息
        LocatonConverter.LatLng latLngBaidu = new LocatonConverter.LatLng(latitudeBaidu,
                longitudeBaidu);
        LocatonConverter.LatLng latLngWgs84 = LocatonConverter.bd09ToWgs84(latLngBaidu);
        latitudeWgs84 = latLngWgs84.getLatitude();
        longitudeWgs84 = latLngWgs84.getLongitude();
        radius = bdLocation.getRadius();    //获取定位精度，默认值为0.0f
        altitude = bdLocation.getAltitude();

        coorType = bdLocation.getCoorType();
        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

        errorCode = bdLocation.getLocType();
        //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

        networkLocationCode = bdLocation.getNetworkLocationType();//在网络定位结果的情况下，获取网络定位结果是通过基站定位得到的还是通过wifi定位得到的还是GPS得结果
        // 返回: String : "wf"： wifi定位结果 “cl“； cell定位结果 “ll”：GPS定位结果 ;null 没有获取到定位结果采用的类型

        province = bdLocation.getProvince();//省
        city = bdLocation.getCity();//市
        district = bdLocation.getDistrict();//区县
        street = bdLocation.getStreet();//街道
        streetNumber = bdLocation.getStreetNumber();//街道号码
        addrStr = bdLocation.getAddrStr();//详细地址

    }
}
