package com.hbmcc.shilinlin.networkoptz.ui.fragment.forth.basestationdatabase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.adapter.BasestationDatabaseFragmentAdapter;
import com.hbmcc.shilinlin.networkoptz.base.BaseBackFragment;

public class BasestationDatabaseFragment extends BaseBackFragment {

    public static final String TAG = BasestationDatabaseFragment.class.getSimpleName();
    private static final int REQ_MODIFY_FRAGMENT = 100;

    private static final String ARG_TITLE = "arg_title";
    static final String KEY_RESULT_TITLE = "title";
    private String mTitle;

    private TabLayout mTab;
    private Toolbar mToolbar;
    private ViewPager mViewPager;

    public static BasestationDatabaseFragment newInstance(String title) {
        BasestationDatabaseFragment fragment = new BasestationDatabaseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(ARG_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basestation_database, container,
                false);
        initView(view);
        return attachToSwipeBack(view);
    }

    private void initView(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        mTab = view.findViewById(R.id.tab_fragment_basestastion_database);
        mViewPager = view.findViewById(R.id.viewpager_fragment_basestastion_database);

        mTab.addTab(mTab.newTab());
        mTab.addTab(mTab.newTab());
        mTab.addTab(mTab.newTab());

        mToolbar.setTitle(mTitle);
        initToolbarNav(mToolbar);
    }

    /**
     * 这里演示:
     * 比较复杂的Fragment页面会在第一次start时,导致动画卡顿
     * Fragmentation提供了onEnterAnimationEnd()方法,该方法会在 入栈动画 结束时回调
     * 所以在onCreateView进行一些简单的View初始化(比如 toolbar设置标题,返回按钮; 显示加载数据的进度条等),
     * 然后在onEnterAnimationEnd()方法里进行 复杂的耗时的初始化 (比如FragmentPagerAdapter的初始化 加载数据等)
     */
    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        initDelayView();
    }

    private void initDelayView() {
        mViewPager.setAdapter(new BasestationDatabaseFragmentAdapter(getChildFragmentManager()
                , "4G", "3G", "2G"));
        mTab.setupWithViewPager(mViewPager);
    }

}
