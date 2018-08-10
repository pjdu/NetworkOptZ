package com.hbmcc.shilinlin.networkoptz.ui.fragment.forth.basestationdatabase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hbmcc.shilinlin.networkoptz.R;

import me.yokeyword.fragmentation.SupportFragment;


public class GsmBasestationDatabaseFragment extends SupportFragment {
    private EditText editTextFragmentLteBasestionDatabaseSearch;
    private Button btnFragmentLteBasestionDatabaseSearch;
    private RecyclerView recyclerviewFragmentLteBasestationDatabase;

    public static GsmBasestationDatabaseFragment newInstance() {
        Bundle args = new Bundle();
        GsmBasestationDatabaseFragment fragment = new GsmBasestationDatabaseFragment();
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
}
