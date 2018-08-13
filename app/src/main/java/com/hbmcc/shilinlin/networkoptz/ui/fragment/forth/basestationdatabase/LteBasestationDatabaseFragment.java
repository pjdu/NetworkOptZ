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

import com.hbmcc.shilinlin.networkoptz.App;
import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.database.LteBasestationCell;
import com.hbmcc.shilinlin.networkoptz.util.FileUtils;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.yokeyword.fragmentation.SupportFragment;


public class LteBasestationDatabaseFragment extends SupportFragment {
    private static final String TAG = "LteBasestationDatabaseF";
    ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
    private EditText editTextFragmentLteBasestionDatabaseSearch;
    private Button btnFragmentLteBasestionDatabaseSearch;
    private RecyclerView recyclerviewFragmentLteBasestationDatabase;
    private LteBasestationCell lteBasestationCell;
    private List<LteBasestationCell> lteBasestationCellList;

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
        if (FileUtils.isFileExist(FileUtils.getLteInputFile())) {
            newCachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    File lteDatabaseFile = new File(FileUtils.getLteInputFile());

                    StringBuilder cSb = new StringBuilder();
                    String inString = "";
                    LitePal.deleteAll(LteBasestationCell.class);

                    int i = 0;
                    try {
                        BufferedReader reader =
                                new BufferedReader(new InputStreamReader(new FileInputStream(lteDatabaseFile), "GBK"));
                        while ((inString = reader.readLine()) != null) {
                            String[] inStringSplit = inString.split(",");
                            if (inStringSplit.length != 18) {
                                _mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(App.getContext(),"导入的工参数据格式不对",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            i++;
                            if (i > 1) {
                                lteBasestationCell = new LteBasestationCell();
                                lteBasestationCell.setEci(Long.parseLong(inStringSplit[0]));
                                lteBasestationCell.setName(inStringSplit[1]);
                                lteBasestationCell.setCity(inStringSplit[2]);
                                lteBasestationCell.setLng(Float.parseFloat(inStringSplit[3]));
                                lteBasestationCell.setLat(Float.parseFloat
                                        (inStringSplit[4]));
                                lteBasestationCell.setEnbCellAltitude(Float.parseFloat(inStringSplit[5]));
                                lteBasestationCell.setEnbCellCoverageType(Integer.parseInt
                                        (inStringSplit[6]));
                                lteBasestationCell.setEnbCellAzimuth(Float.parseFloat(inStringSplit[7]));
                                lteBasestationCell.setEnbCellMechanicalDipAngle(Float.parseFloat
                                        (inStringSplit[8]));
                                lteBasestationCell.setEnbCellElectronicDipAngle(Float.parseFloat
                                        (inStringSplit[9]));
                                lteBasestationCell.setEnbCellManufactoryName(inStringSplit[10]);
                                lteBasestationCell.setTac(Integer.parseInt
                                        (inStringSplit[11]));
                                lteBasestationCell.setPci(Integer.parseInt
                                        (inStringSplit[12]));
                                lteBasestationCell.setLteEarFcn(Integer.parseInt
                                        (inStringSplit[13]));
                                lteBasestationCell.setEnbId((int) (Long.parseLong(inStringSplit[0]) / 256));
                                lteBasestationCell.setEnbCellId((int) (Long.parseLong(inStringSplit[0]) %
                                        256));
                                lteDatabaseList.add(lteBasestationCell);

                            }
                        }
                        LitePal.saveAll(lteDatabaseList);
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "4G基站数据库导入成功", Toast.LENGTH_LONG).show()
                            ;//显示
                        }
                    });
                }
            });


        } else {
            Toast.makeText(getContext(), "4G基站数据库文件不存在", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
