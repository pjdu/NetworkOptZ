package com.hbmcc.shilinlin.networkoptz.ui.fragment.first;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.base.BaseMainFragment;
import com.hbmcc.shilinlin.networkoptz.event.TabSelectedEvent;
import com.hbmcc.shilinlin.networkoptz.event.UpdateUEStatusEvent;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.MainFragment;
import com.hbmcc.shilinlin.networkoptz.util.NumberFormat;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

public class FirstTabFragment extends BaseMainFragment {

    private Toolbar toolbarMain;
    private TextView textViewMainActivityOperator;
    private TextView textViewMainActivityIMSI;
    private TextView textViewMainActivityIMEI;
    private TextView textViewMainActivityUEModel;
    private TextView textViewMainActivityAndroidVersion;
    private TextView textViewMainActivityCurrentLocName;
    private TextView textViewMainActivityLongitude;
    private TextView textViewMainActivityLatitude;
    private TextView textViewMainActivityTAC;
    private TextView textViewMainActivityPCI;
    private TextView textViewMainActivityCGI;
    private TextView textViewMainActivityEarFcn;
    private TextView textViewMainActivityBand;
    private TextView textViewMainActivityFrequency;
    private TextView textViewMainActivityRSRP;
    private TextView textViewMainActivityRSRQ;
    private TextView textViewMainActivitySINR;
    private TextView textViewMainActivityAltitude;
    private TextView textViewMainActivityCellChsName;
    private TextView textViewFragmentFirstTabRecentAvgSignalStrength;
    private RecyclerView recyclerViewFragmentFirstTabRecentRecord;
    private RecyclerView recyclerViewFragmentFirstTabNeighbourCellInfo;

    public static FirstTabFragment newInstance() {
        Bundle args = new Bundle();
        FirstTabFragment fragment = new FirstTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_tab, container,
                false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        EventBusActivityScope.getDefault(_mActivity).register(this);

        toolbarMain = view.findViewById(R.id.toolbar_main);
        textViewMainActivityOperator = view.findViewById(R.id.textView_fragment_first_tab_operator);
        textViewMainActivityIMSI = view.findViewById(R.id.textView_fragment_first_tab_IMSI);
        textViewMainActivityIMEI = view.findViewById(R.id.textView_fragment_first_tab_IMEI);
        textViewMainActivityUEModel = view.findViewById(R.id.textView_fragment_first_tab_uemodel);
        textViewMainActivityAndroidVersion = view.findViewById(R.id.textView_fragment_first_tab_androidversion);
        textViewMainActivityCurrentLocName = view.findViewById(R.id.textView_fragment_first_tab_currentlocname);
        textViewMainActivityLongitude = view.findViewById(R.id.textView_fragment_first_tab_longitude);
        textViewMainActivityLatitude = view.findViewById(R.id.textView_fragment_first_tab_latitude);
        textViewMainActivityTAC = view.findViewById(R.id.textView_fragment_first_tab_tac);
        textViewMainActivityPCI = view.findViewById(R.id.textView_fragment_first_tab_pci);
        textViewMainActivityCGI = view.findViewById(R.id.textView_fragment_first_tab_cgi);
        textViewMainActivityEarFcn = view.findViewById(R.id.textView_fragment_first_tab_earfcn);
        textViewMainActivityBand = view.findViewById(R.id.textView_fragment_first_tab_band);
        textViewMainActivityFrequency = view.findViewById(R.id.textView_fragment_first_tab_frequency);
        textViewMainActivityRSRP = view.findViewById(R.id.textView_fragment_first_tab_RSRP);
        textViewMainActivityRSRQ = view.findViewById(R.id.textView_fragment_first_tab_RSRQ);
        textViewMainActivitySINR = view.findViewById(R.id.textView_fragment_first_tab_SINR);
        textViewMainActivityAltitude = view.findViewById(R.id.textView_fragment_first_tab_altitude);
        textViewMainActivityCellChsName = view.findViewById(R.id.textView_fragment_first_tab_cellchsname);
        textViewFragmentFirstTabRecentAvgSignalStrength = view.findViewById(R.id.textView_fragment_first_tab_recent_avg_signal_strength);
        recyclerViewFragmentFirstTabRecentRecord = view.findViewById(R.id.recyclerView_fragment_first_tab_recent_record);
        recyclerViewFragmentFirstTabNeighbourCellInfo = view.findViewById(R.id.recyclerView_fragment_first_tab_neighbour_cell_info);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

    /**
     * Reselected Tab
     */
    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {
        if (event.position != MainFragment.FIRST) return;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateView(UpdateUEStatusEvent updateUEStatusEvent) {
        textViewMainActivityOperator.setText(updateUEStatusEvent.ueStatus.locationStatus
                .operators+"");
        textViewMainActivityIMSI.setText(updateUEStatusEvent.ueStatus.networkStatus.IMSI+"");
        textViewMainActivityIMEI.setText(updateUEStatusEvent.ueStatus.networkStatus.IMEI+"");
        textViewMainActivityUEModel.setText(updateUEStatusEvent.ueStatus.networkStatus.hardwareModel+"");
        textViewMainActivityAndroidVersion.setText(updateUEStatusEvent.ueStatus.networkStatus.androidVersion+"");
        textViewMainActivityLongitude.setText(NumberFormat.doubleFormat(updateUEStatusEvent.ueStatus.locationStatus
                .longitudeWgs84,5)+"");
        textViewMainActivityLatitude.setText(NumberFormat.doubleFormat
                (updateUEStatusEvent.ueStatus.locationStatus
                .latitudeWgs84,5)+ "");
        textViewMainActivityAltitude.setText(updateUEStatusEvent.ueStatus.locationStatus.altitude+"米");
        textViewMainActivityCurrentLocName.setText(updateUEStatusEvent.ueStatus.locationStatus
                .city+updateUEStatusEvent.ueStatus.locationStatus.district+updateUEStatusEvent
                .ueStatus.locationStatus.street+updateUEStatusEvent.ueStatus.locationStatus
                .streetNumber);

        textViewMainActivityCellChsName.setText("待开发");
        textViewMainActivityTAC.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.tac+"");
        textViewMainActivityPCI.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.pci+"");
        textViewMainActivityCGI.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.enbId + "-" + updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.enbCellId);
        textViewMainActivityEarFcn.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.lteEarFcn+"");
        textViewMainActivityBand.setText("待开发");
        textViewMainActivityFrequency.setText("待开发");
        textViewMainActivityRSRP.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.signalStrength+"");
        textViewMainActivityRSRQ.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.rsrq+"");
        textViewMainActivitySINR.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.sinr+"");
    }


}
