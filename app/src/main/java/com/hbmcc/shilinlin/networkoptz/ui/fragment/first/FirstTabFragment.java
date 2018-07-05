package com.hbmcc.shilinlin.networkoptz.ui.fragment.first;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hbmcc.shilinlin.networkoptz.Adapter.RecentRecordAdapter;
import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.base.BaseMainFragment;
import com.hbmcc.shilinlin.networkoptz.event.TabSelectedEvent;
import com.hbmcc.shilinlin.networkoptz.event.UpdateUEStatusEvent;
import com.hbmcc.shilinlin.networkoptz.telephony.NetworkStatus;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.MainFragment;
import com.hbmcc.shilinlin.networkoptz.util.NumberFormat;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

public class FirstTabFragment extends BaseMainFragment {
    private static final String TAG = "FirstTabFragment";
    private Toolbar toolbarMain;
    private TextView textViewFragmentFirstTabOperator;
    private TextView textViewFragmentFirstTabIMSI;
    private TextView textViewFragmentFirstTabIMEI;
    private TextView textViewFragmentFirstTabUEModel;
    private TextView textViewFragmentFirstTabAndroidVersion;
    private TextView textViewFragmentFirstTabCurrentLocName;
    private TextView textViewFragmentFirstTabLongitude;
    private TextView textViewFragmentFirstTabLatitude;
    private TextView textViewFragmentFirstTabTAC;
    private TextView textViewFragmentFirstTabPCI;
    private TextView textViewFragmentFirstTabCGI;
    private TextView textViewFragmentFirstTabEarFcn;
    private TextView textViewFragmentFirstTabBand;
    private TextView textViewFragmentFirstTabFrequency;
    private TextView textViewFragmentFirstTabRSRP;
    private TextView textViewFragmentFirstTabRSRQ;
    private TextView textViewFragmentFirstTabSINR;
    private TextView textViewFragmentFirstTabAltitude;
    private TextView textViewFragmentFirstTabCellChsName;
    private TextView textViewFragmentFirstTabRecentAvgSignalStrength;
    private RecyclerView recyclerViewFragmentFirstTabRecentRecord;
    private RecyclerView recyclerViewFragmentFirstTabNeighbourCellInfo;
    private TextView textViewFragmentFirstTabCurrentDateTime;
    private List<NetworkStatus> recentNetworkStatusRecordList;
    RecentRecordAdapter recentRecordAdapter;
    NetworkStatus mNewNetworkStatus;

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
        textViewFragmentFirstTabOperator = view.findViewById(R.id.textView_fragment_first_tab_operator);
        textViewFragmentFirstTabIMSI = view.findViewById(R.id.textView_fragment_first_tab_IMSI);
        textViewFragmentFirstTabIMEI = view.findViewById(R.id.textView_fragment_first_tab_IMEI);
        textViewFragmentFirstTabUEModel = view.findViewById(R.id.textView_fragment_first_tab_uemodel);
        textViewFragmentFirstTabAndroidVersion = view.findViewById(R.id.textView_fragment_first_tab_androidversion);
        textViewFragmentFirstTabCurrentLocName = view.findViewById(R.id.textView_fragment_first_tab_currentlocname);
        textViewFragmentFirstTabLongitude = view.findViewById(R.id.textView_fragment_first_tab_longitude);
        textViewFragmentFirstTabLatitude = view.findViewById(R.id.textView_fragment_first_tab_latitude);
        textViewFragmentFirstTabTAC = view.findViewById(R.id.textView_fragment_first_tab_tac);
        textViewFragmentFirstTabPCI = view.findViewById(R.id.textView_fragment_first_tab_pci);
        textViewFragmentFirstTabCGI = view.findViewById(R.id.textView_fragment_first_tab_cgi);
        textViewFragmentFirstTabEarFcn = view.findViewById(R.id.textView_fragment_first_tab_earfcn);
        textViewFragmentFirstTabBand = view.findViewById(R.id.textView_fragment_first_tab_band);
        textViewFragmentFirstTabFrequency = view.findViewById(R.id.textView_fragment_first_tab_frequency);
        textViewFragmentFirstTabRSRP = view.findViewById(R.id.textView_fragment_first_tab_RSRP);
        textViewFragmentFirstTabRSRQ = view.findViewById(R.id.textView_fragment_first_tab_RSRQ);
        textViewFragmentFirstTabSINR = view.findViewById(R.id.textView_fragment_first_tab_SINR);
        textViewFragmentFirstTabAltitude = view.findViewById(R.id.textView_fragment_first_tab_altitude);
        textViewFragmentFirstTabCellChsName = view.findViewById(R.id.textView_fragment_first_tab_cellchsname);
        textViewFragmentFirstTabRecentAvgSignalStrength = view.findViewById(R.id.textView_fragment_first_tab_recent_avg_signal_strength);
        textViewFragmentFirstTabCurrentDateTime = view.findViewById(R.id
                .textView_fragment_first_tab_currentDateTime);
        recyclerViewFragmentFirstTabRecentRecord = view.findViewById(R.id.recyclerView_fragment_first_tab_recent_record);
        recyclerViewFragmentFirstTabNeighbourCellInfo = view.findViewById(R.id.recyclerView_fragment_first_tab_neighbour_cell_info);
        recentNetworkStatusRecordList = new ArrayList<>();
        initRecyclerViewRecentRecord();
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
        textViewFragmentFirstTabOperator.setText(updateUEStatusEvent.ueStatus.locationStatus
                .operators+"");
        textViewFragmentFirstTabIMSI.setText(updateUEStatusEvent.ueStatus.networkStatus.IMSI+"");
        textViewFragmentFirstTabIMEI.setText(updateUEStatusEvent.ueStatus.networkStatus.IMEI+"");
        textViewFragmentFirstTabUEModel.setText(updateUEStatusEvent.ueStatus.networkStatus.hardwareModel+"");
        textViewFragmentFirstTabAndroidVersion.setText(updateUEStatusEvent.ueStatus.networkStatus.androidVersion+"");
        textViewFragmentFirstTabLongitude.setText(NumberFormat.doubleFormat(updateUEStatusEvent.ueStatus.locationStatus
                .longitudeWgs84,5)+"");
        textViewFragmentFirstTabLatitude.setText(NumberFormat.doubleFormat
                (updateUEStatusEvent.ueStatus.locationStatus
                .latitudeWgs84,5)+ "");
        textViewFragmentFirstTabAltitude.setText(updateUEStatusEvent.ueStatus.locationStatus.altitude+"米");
        textViewFragmentFirstTabCurrentLocName.setText(updateUEStatusEvent.ueStatus.locationStatus
                .city+updateUEStatusEvent.ueStatus.locationStatus.district+updateUEStatusEvent
                .ueStatus.locationStatus.street+updateUEStatusEvent.ueStatus.locationStatus
                .streetNumber);

        textViewFragmentFirstTabCellChsName.setText("待开发");
        textViewFragmentFirstTabTAC.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.tac+"");
        textViewFragmentFirstTabPCI.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.pci+"");
        textViewFragmentFirstTabCGI.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.enbId + "-" + updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.enbCellId);
        textViewFragmentFirstTabEarFcn.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.lteEarFcn+"");
        textViewFragmentFirstTabBand.setText("待开发");
        textViewFragmentFirstTabFrequency.setText("待开发");
        textViewFragmentFirstTabRSRP.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.signalStrength+"");
        textViewFragmentFirstTabRSRQ.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.rsrq+"");
        textViewFragmentFirstTabSINR.setText(updateUEStatusEvent.ueStatus.networkStatus
                .lteServingCellTower.sinr+"");
        textViewFragmentFirstTabCurrentDateTime.setText(updateUEStatusEvent.ueStatus
                .networkStatus.time);
        mNewNetworkStatus = new NetworkStatus();
        mNewNetworkStatus = updateUEStatusEvent.ueStatus.networkStatus;
        recentNetworkStatusRecordList.add(mNewNetworkStatus);

//        for(NetworkStatus kk:recentNetworkStatusRecordList) {
//            Log.d(TAG, "updateView: time" + kk.time);
//        }
        recentRecordAdapter.notifyDataSetChanged();
    }

    private void initRecyclerViewRecentRecord(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewFragmentFirstTabRecentRecord.setLayoutManager(layoutManager);
        recentRecordAdapter = new RecentRecordAdapter(recentNetworkStatusRecordList);
        recyclerViewFragmentFirstTabRecentRecord.setAdapter(recentRecordAdapter);

    }
}
