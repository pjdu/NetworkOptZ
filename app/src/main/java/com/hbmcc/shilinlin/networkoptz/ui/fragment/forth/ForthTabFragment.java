package com.hbmcc.shilinlin.networkoptz.ui.fragment.forth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.base.BaseMainFragment;
import com.hbmcc.shilinlin.networkoptz.event.TabSelectedEvent;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.MainFragment;
import com.hbmcc.shilinlin.networkoptz.ui.fragment.first.FirstTabFragment;

import org.greenrobot.eventbus.Subscribe;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;

public class ForthTabFragment extends BaseMainFragment {
    public static ForthTabFragment newInstance() {

        Bundle args = new Bundle();

        ForthTabFragment fragment = new ForthTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forth_tab, container,
                false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        EventBusActivityScope.getDefault(_mActivity).register(this);

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
        if (event.position != MainFragment.FORTH) return;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }
}
