package com.hbmcc.shilinlin.networkoptz.event;

import com.hbmcc.shilinlin.networkoptz.telephony.UeStatus;

public class UpdateUeStatusEvent {
    public UeStatus ueStatus;

    public UpdateUeStatusEvent(UeStatus ueStatus){
        this.ueStatus = ueStatus;
    }
}
