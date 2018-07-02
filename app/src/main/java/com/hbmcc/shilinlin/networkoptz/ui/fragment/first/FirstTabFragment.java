package com.hbmcc.shilinlin.networkoptz.ui.fragment.first;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.MyLocationData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.base.BaseMainFragment;
import com.hbmcc.shilinlin.networkoptz.event.TabSelectedEvent;
import com.hbmcc.shilinlin.networkoptz.event.UpdateLocationStatusEvent;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.MainFragment;
import com.hbmcc.shilinlin.networkoptz.util.NumberFormat;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

public class FirstTabFragment extends BaseMainFragment {

    LineChart mChart;
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

        /*
        如果是从xml创建，就直接用以下方法
        LineChart chart = (LineChart) findViewById(R.id.chart);
        如果需要动态创建，用以下方法
        LineChart chart = new LineChart(Context);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativeLayout);
        rl.add(chart);
        */
        mChart = view.findViewById(R.id.chart_MainActivity_lineChart);
        toolbarMain = view.findViewById(R.id.toolbar_main);
        textViewMainActivityOperator = view.findViewById(R.id.textView_MainActivity_Operator);
        textViewMainActivityIMSI = view.findViewById(R.id.textView_MainActivity_IMSI);
        textViewMainActivityIMEI = view.findViewById(R.id.textView_MainActivity_IMEI);
        textViewMainActivityUEModel = view.findViewById(R.id.textView_MainActivity_UEModel);
        textViewMainActivityAndroidVersion = view.findViewById(R.id.textView_MainActivity_AndroidVersion);
        textViewMainActivityCurrentLocName = view.findViewById(R.id.textView_MainActivity_CurrentLocName);
        textViewMainActivityLongitude = view.findViewById(R.id.textView_MainActivity_Longitude);
        textViewMainActivityLatitude = view.findViewById(R.id.textView_MainActivity_Latitude);
        textViewMainActivityTAC = view.findViewById(R.id.textView_MainActivity_TAC);
        textViewMainActivityPCI = view.findViewById(R.id.textView_MainActivity_PCI);
        textViewMainActivityCGI = view.findViewById(R.id.textView_MainActivity_CGI);
        textViewMainActivityEarFcn = view.findViewById(R.id.textView_MainActivity_earFcn);
        textViewMainActivityBand = view.findViewById(R.id.textView_MainActivity_Band);
        textViewMainActivityFrequency = view.findViewById(R.id.textView_MainActivity_Frequency);
        textViewMainActivityRSRP = view.findViewById(R.id.textView_MainActivity_RSRP);
        textViewMainActivityRSRQ = view.findViewById(R.id.textView_MainActivity_RSRQ);
        textViewMainActivitySINR = view.findViewById(R.id.textView_MainActivity_SINR);
        textViewMainActivityAltitude = view.findViewById(R.id.textView_MainActivity_Altitude);
        initChart();
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
    public void updateLocation(UpdateLocationStatusEvent updateLocationStatusEvent) {
        textViewMainActivityLatitude.setText(NumberFormat.doubleFormat(updateLocationStatusEvent.locationStatus
                .latitudeWgs84,5)+ "");
        textViewMainActivityLongitude.setText(NumberFormat.doubleFormat(updateLocationStatusEvent.locationStatus
                .longitudeWgs84,5)+"");
        textViewMainActivityAltitude.setText(updateLocationStatusEvent.locationStatus.altitude+"米");
        textViewMainActivityCurrentLocName.setText(updateLocationStatusEvent.locationStatus.addrStr);
    }

    private void initChart() {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < 40; i++) {

            float val = (float) (Math.random() * 50 - 144);
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setDrawIcons(false);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data

            mChart.setData(data);

        }
    }
}
