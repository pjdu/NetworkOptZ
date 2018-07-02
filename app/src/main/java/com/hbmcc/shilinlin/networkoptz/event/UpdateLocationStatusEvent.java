package com.hbmcc.shilinlin.networkoptz.event;

import com.baidu.location.BDLocation;
import com.hbmcc.shilinlin.networkoptz.telephony.LocationStatus;

public class UpdateLocationStatusEvent {
    public LocationStatus locationStatus;

    public UpdateLocationStatusEvent(BDLocation bdLocation){
        locationStatus = new LocationStatus(bdLocation);
    }
}
