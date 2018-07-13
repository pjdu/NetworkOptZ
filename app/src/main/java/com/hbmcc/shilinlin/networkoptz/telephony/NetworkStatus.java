package com.hbmcc.shilinlin.networkoptz.telephony;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.hbmcc.shilinlin.networkoptz.App;
import com.hbmcc.shilinlin.networkoptz.telephony.cellinfo.CellInfo;
import com.hbmcc.shilinlin.networkoptz.telephony.cellinfo.GsmCellInfo;
import com.hbmcc.shilinlin.networkoptz.telephony.cellinfo.LteCellInfo;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NetworkStatus {
    private static final String TAG = "NetworkInfo";
    public static final int NETWORK_STATUS_ERROR = 9999;

    public String time;
    public int networkType;
    public String imei;
    public String imsi;
    public String androidVersion;
    public String hardwareModel;
    public LteCellInfo lteServingCellTower;
    public GsmCellInfo gsmServingCellTower;
    public ArrayList<LteCellInfo> lteNeighbourCellTowers;
    public ArrayList<GsmCellInfo> gsmNeighbourCellTowers;

    public NetworkStatus() {
        TelephonyManager mTelephonyManager = (TelephonyManager) App.getContext().getSystemService(Context
                .TELEPHONY_SERVICE);
        if (mTelephonyManager == null) {
            Toast.makeText(App.getContext(), "获取手机网络存在问题", Toast.LENGTH_SHORT).show();
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss ");
        time = sDateFormat.format(new java.util.Date());
        networkType = determineNetworkType(App.getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imei = mTelephonyManager.getImei();
        } else {
            imei = mTelephonyManager.getDeviceId();
        }
        imsi = mTelephonyManager.getSubscriberId();


        androidVersion = Build.VERSION.RELEASE;
        hardwareModel = Build.MODEL;

        // 获取基站信息
        //SDK18及之后android系统使用getAllCellInfo方法，并且对基站的类型加以区分
        List<android.telephony.CellInfo> infos = mTelephonyManager.getAllCellInfo();
        if (infos != null && infos.size() != 0) {
            lteNeighbourCellTowers = new ArrayList<>();
            gsmNeighbourCellTowers = new ArrayList<>();
            for (android.telephony.CellInfo i : infos) { // 根据邻区总数进行循环
                if (i instanceof CellInfoLte) {
                    LteCellInfo tower = new LteCellInfo();
                    CellIdentityLte cellIdentityLte = ((CellInfoLte) i).getCellIdentity();
                    tower.cellType = CellInfo.STRING_TYPE_LTE;
                    tower.isRegitered = i.isRegistered();
                    tower.tac = cellIdentityLte.getTac();
                    tower.mobileCountryCode = cellIdentityLte.getMcc();
                    tower.mobileNetworkCode = cellIdentityLte.getMnc();
                    tower.cellId = cellIdentityLte.getCi();
                    tower.signalStrength = ((CellInfoLte) i).getCellSignalStrength().getDbm();
                    tower.timingAdvance = ((CellInfoLte) i).getCellSignalStrength().getTimingAdvance();
                    if (Build.VERSION.SDK_INT >= 24) {
                        tower.lteEarFcn = cellIdentityLte.getEarfcn();
                    } else {
                        try {
                            Class<?> cellIdentityLteClass = cellIdentityLte.getClass();
                            Method methodGetEarFcn = cellIdentityLteClass.getDeclaredMethod
                                    ("getEarfcn");
                            tower.lteEarFcn = (int) methodGetEarFcn.invoke(cellIdentityLte);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 26) {
                        tower.rsrq = ((CellInfoLte) i).getCellSignalStrength().getRsrq();
                        tower.sinr = ((CellInfoLte) i).getCellSignalStrength().getRssnr();
                    } else {
                        try {
                            Class<?> cellSignalStrengthLteClass = ((CellInfoLte) i)
                                    .getCellSignalStrength().getClass();
                            Method methodGetRsrq = cellSignalStrengthLteClass.getDeclaredMethod
                                    ("getRsrq");
                            Method methodGetRssnr = cellSignalStrengthLteClass.getDeclaredMethod
                                    ("getRssnr");
                            tower.rsrq = (int) methodGetRsrq.invoke(((CellInfoLte) i)
                                    .getCellSignalStrength());
                            tower.sinr = (int) methodGetRssnr.invoke(((CellInfoLte) i).getCellSignalStrength());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (tower.sinr == Integer.MAX_VALUE) {
                        tower.sinr = NetworkStatus.NETWORK_STATUS_ERROR;
                    }
                    if (tower.rsrq == Integer.MAX_VALUE) {
                        tower.rsrq = NetworkStatus.NETWORK_STATUS_ERROR;
                    }
                    if (tower.tac == Integer.MAX_VALUE) {
                        tower.tac = NetworkStatus.NETWORK_STATUS_ERROR;
                    }
                    tower.pci = cellIdentityLte.getPci();
                    tower.enbId = (int) Math.floor(tower.cellId / 256);
                    tower.enbCellId = tower.cellId % 256;
                    if (i.isRegistered()) {
                        lteServingCellTower = tower;
                    } else {
                        lteNeighbourCellTowers.add(tower);
                    }
                } else if (i instanceof CellInfoGsm) {
                    GsmCellInfo tower = new GsmCellInfo();
                    CellIdentityGsm cellIdentityGsm = ((CellInfoGsm) i).getCellIdentity();//从这个类里面可以取出好多有用的东西
                    if (cellIdentityGsm == null) {
                        continue;
                    }
                    tower.cellType = CellInfo.STRING_TYPE_GSM;
                    tower.isRegitered = i.isRegistered();
                    tower.locationAreaCode = cellIdentityGsm.getLac();
                    tower.mobileCountryCode = cellIdentityGsm.getMcc();
                    tower.mobileNetworkCode = cellIdentityGsm.getMnc();
                    tower.signalStrength = ((CellInfoGsm) i).getCellSignalStrength().getDbm();
                    tower.timingAdvance = 0;
                    tower.gsmCellId = cellIdentityGsm.getCid();
                    tower.psc = cellIdentityGsm.getPsc();
                    if (Build.VERSION.SDK_INT >= 24) {
                        tower.bsic = cellIdentityGsm.getBsic();
                        tower.gsmArFcn = cellIdentityGsm.getArfcn();
                    }
                    if (i.isRegistered()) {
                        gsmServingCellTower = tower;
                    } else {
                        gsmNeighbourCellTowers.add(tower);
                    }
                } else {
                    Log.i(TAG, "不知道现在是啥基站");
                }
            }
        }
    }

    private int determineNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return CellInfo.TYPE_UNKNOWN;
        }
        android.net.NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if (network == null) {
            return CellInfo.TYPE_UNKNOWN;
        }
        switch (network.getSubtype()) {
            case TelephonyManager.NETWORK_TYPE_LTE:
                return CellInfo.TYPE_LTE;
            case TelephonyManager.NETWORK_TYPE_GSM:
                return CellInfo.TYPE_GSM;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return CellInfo.TYPE_GSM;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return CellInfo.TYPE_CDMA;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return CellInfo.TYPE_GSM;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return CellInfo.TYPE_WCDMA;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return CellInfo.TYPE_WCDMA;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return CellInfo.TYPE_WCDMA;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return CellInfo.TYPE_WCDMA;
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return CellInfo.TYPE_WCDMA;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return CellInfo.TYPE_WCDMA;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return CellInfo.TYPE_WCDMA;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return CellInfo.TYPE_WCDMA;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return CellInfo.TYPE_WCDMA;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                break;
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                break;
            default:
                return CellInfo.TYPE_UNKNOWN;
        }
        return CellInfo.TYPE_UNKNOWN;
    }

}
