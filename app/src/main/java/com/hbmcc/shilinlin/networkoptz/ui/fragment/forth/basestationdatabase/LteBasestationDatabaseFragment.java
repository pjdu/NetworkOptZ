package com.hbmcc.shilinlin.networkoptz.ui.fragment.forth.basestationdatabase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.util.FileUtils;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import me.yokeyword.fragmentation.SupportFragment;


public class LteBasestationDatabaseFragment extends SupportFragment {
    private EditText editTextFragmentLteBasestionDatabaseSearch;
    private Button btnFragmentLteBasestionDatabaseSearch;
    private RecyclerView recyclerviewFragmentLteBasestationDatabase;

    public static LteBasestationDatabaseFragment newInstance() {
        Bundle args = new Bundle();
        LteBasestationDatabaseFragment fragment = new LteBasestationDatabaseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lte_basestation_database, container,
                false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        editTextFragmentLteBasestionDatabaseSearch = view.findViewById(R.id.editText_fragment_lte_basestion_database_search);
        btnFragmentLteBasestionDatabaseSearch = view.findViewById(R.id.btn_fragment_lte_basestion_database_search);
        recyclerviewFragmentLteBasestationDatabase = view.findViewById(R.id.recyclerview_fragment_lte_basestation_database);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public boolean importLteDatabase() {
        if (FileUtils.isFileExist(FileUtils.getLteinputFile())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File lteDatabaseFile = new File(FileUtils.getLteinputFile());
                    StringBuilder cSb = new StringBuilder();
                    String inString = "";
                    LitePal.deleteAll(LteDatabase.class);

                    int i = 0;
                    try {
                        BufferedReader reader =
                                new BufferedReader(new InputStreamReader(new FileInputStream(lteDatabaseFile), "GBK"));

                        while ((inString = reader.readLine()) != null) {

                            String[] inStringSplit = inString.split(",");
                            if (inStringSplit.length != 14) {
                                return;
                            }
                            i++;
                            if (i > 1) {
                                lteDatabase = new LteDatabase();
                                lteDatabase.setCellId(Long.parseLong(inStringSplit[0]));
                                lteDatabase.setEnbCellName(inStringSplit[1]);
                                lteDatabase.setEnbCellLng(Float.parseFloat(inStringSplit[2]));
                                lteDatabase.setEnbCellLat(Float.parseFloat(inStringSplit[3]));
                                lteDatabase.setEnbCellAntennaHeight(Float.parseFloat
                                        (inStringSplit[4]));
                                lteDatabase.setEnbCellAltitude(Float.parseFloat(inStringSplit[5]));
                                lteDatabase.setEnbCellCoverageType(Integer.parseInt
                                        (inStringSplit[6]));
                                lteDatabase.setEnbCellAzimuth(Float.parseFloat(inStringSplit[7]));
                                lteDatabase.setEnbCellMechanicalDipAngle(Float.parseFloat
                                        (inStringSplit[8]));
                                lteDatabase.setEnbCellElectronicDipAngle(Float.parseFloat
                                        (inStringSplit[9]));
                                lteDatabase.setEnbCellManufactoryName(inStringSplit[10]);
                                lteDatabase.setTac(Integer.parseInt
                                        (inStringSplit[11]));
                                lteDatabase.setPci(Integer.parseInt
                                        (inStringSplit[12]));
                                lteDatabase.setLteEarFcn(Integer.parseInt
                                        (inStringSplit[13]));
                                lteDatabase.setEnbId((int) (Long.parseLong(inStringSplit[0]) / 256));
                                lteDatabase.setEnbCellId((int) (Long.parseLong(inStringSplit[0]) %
                                        256));
                                lteDatabaseList.add(lteDatabase);

                            }
                        }
                        LitePal.saveAll(lteDatabaseList);

                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("BSDA2", "第" + i + "行导入失败，该行数据为" + inString);
                    }

                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "4G基站数据库导入成功", Toast.LENGTH_LONG).show()
                            ;//显示
                            Log.i("BSDA2", "4G基站数据库导入成功");

                        }
                    });
                }
            }).start();


        } else {
            Toast.makeText(getContext(), "4G基站数据库文件不存在", Toast.LENGTH_LONG).show();
            Log.i("BSDA", "4G基站数据库文件不存在");
            return false;
        }
        return true;
    }
}
