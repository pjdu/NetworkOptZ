package com.hbmcc.shilinlin.networkoptz.ui.fragment.forth;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbmcc.shilinlin.networkoptz.MainActivity;
import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.base.BaseBackFragment;
import com.hbmcc.shilinlin.networkoptz.service.AutoUpdateService;

public class AboutFragment extends BaseBackFragment {

    private static final String TAG = "AboutFragment";

    private static final String ARG_TITLE = "arg_title";
    private String mTitle;

    private Toolbar mToolbar;
    private ImageView imageviewUpdateurl;
    private TextView textViewFragmentAboutNewVersion;

    public static AboutFragment newInstance(String title) {

        AboutFragment fragment = new AboutFragment();
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
        View view = inflater.inflate(R.layout.fragment_about, container,
                false);
        initView(view);
        return attachToSwipeBack(view);
    }

    private void initView(View view) {
        mToolbar = view.findViewById(R.id.toolbar);
        imageviewUpdateurl = view.findViewById(R.id.imageview_updateurl);
        textViewFragmentAboutNewVersion = view.findViewById(R.id.textView_fragment_about_newVersion);


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
        imageviewUpdateurl.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri uri = Uri.parse("https://github.com/FFWeather/NetworkOptZ/raw/master/app/release/app-release.apk");
                intent.setData(uri);
                startActivity(intent);
            }
        });


    }



}
