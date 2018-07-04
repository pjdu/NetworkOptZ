package com.hbmcc.shilinlin.networkoptz.telephony;

public class UEStatus {
    public NetworkStatus networkStatus;
    public LocationStatus locationStatus;
    public DownloadSpeedStatus downloadSpeedStatus;
    public UploadSpeedStatus uploadSpeedStatus;

    public UEStatus(NetworkStatus networkStatus, LocationStatus locationStatus, DownloadSpeedStatus downloadSpeedStatus, UploadSpeedStatus uploadSpeedStatus) {
        this.networkStatus = networkStatus;
        this.locationStatus = locationStatus;
        this.downloadSpeedStatus = downloadSpeedStatus;
        this.uploadSpeedStatus = uploadSpeedStatus;
    }
}
