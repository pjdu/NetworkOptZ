package com.hbmcc.shilinlin.networkoptz.event;

import com.hbmcc.shilinlin.networkoptz.telephony.UEStatus;

public class UpdateUEStatusEvent {
    public UEStatus ueStatus;

    public UpdateUEStatusEvent(UEStatus ueStatus){
        this.ueStatus = ueStatus;
    }
}
