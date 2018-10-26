package com.hbmcc.shilinlin.networkoptz.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.hbmcc.shilinlin.networkoptz.App;
import com.hbmcc.shilinlin.networkoptz.R;
import com.hbmcc.shilinlin.networkoptz.listener.DownloadListener;
import com.hbmcc.shilinlin.networkoptz.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;


public class AutoUpdateService extends Service {
    AlertDialog.Builder alertDialog;
    private DownloadTask downloadTask;
    AutoUpdateBinder autoUpdateBinder = new AutoUpdateBinder();
    private String downloadUrl;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NewVersion newVersion = autoUpdateBinder.getNewerVersion();
        if (newVersion != null) {
            if (Integer.valueOf(newVersion.serverVersion) > Integer.valueOf(App.getContext()
                    .getString(R.string.currentVersion))) {
                alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(newVersion.upgradeTitle)
                        .setMessage("新版本号：" + newVersion.serverVersion + "，该版本新功能：" + newVersion.upgradeInfo)
                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                if (!newVersion.lastForce.equals("1")) {
                    alertDialog.setCancelable(true);
                    alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
                else {
                    alertDialog.setCancelable(false);
                }
                alertDialog.show();

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onProgerss(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            autoUpdateBinder.installapk();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
        }

        @Override
        public void onPaused() {
            downloadTask = null;
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return autoUpdateBinder;
    }

    class NewVersion {
        String appName;
        String serverVersion;
        String serverFlag;
        String lastForce;
        String downloadUrl;
        String upgradeTitle;
        String upgradeInfo;
    }

    public class AutoUpdateBinder extends Binder {
        public NewVersion getNewerVersion() {
            final NewVersion newVersion = new NewVersion();
            HttpUtil.sendOkHttpRequest(App.getContext().getString(R.string.autoUpdateJsonUrl),
                    new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String responseData = response.body().toString();
                                JSONArray jsonArray = new JSONArray(responseData);
                                if (jsonArray.length() > 0) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    newVersion.appName = jsonObject.getString("appName");
                                    newVersion.serverVersion = jsonObject.getString("serverVersion");
                                    newVersion.serverFlag = jsonObject.getString("serverFlag");
                                    newVersion.lastForce = jsonObject.getString("lastForce");
                                    newVersion.downloadUrl = jsonObject.getString("downloadUrl");
                                    newVersion.upgradeTitle = jsonObject.getString("upgradeTitle");
                                    newVersion.upgradeInfo = jsonObject.getString("upgradeInfo");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(AutoUpdateService.this, "获取最新版本失败，请检查网络连接", Toast
                                    .LENGTH_LONG).show();
                        }
                    });
            return newVersion;

        }

        public boolean startDownloadNewVerion(NewVersion newVersion) {
            if (downloadTask == null) {
                downloadUrl = newVersion.downloadUrl;
                downloadTask = new DownloadTask(downloadListener);
                downloadTask.execute(downloadUrl);
                startForeground(1, getNotification("Downloading", 0));
            }
            return false;
        }

        public void pauseDownloadNewVerion() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownloadNewVerion() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment
                            .DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + filename);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                }
            }
        }

        private void installapk() {
            String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment
                    .DIRECTORY_DOWNLOADS).getPath();
            File file = new File(directory + filename);
            if (file.exists()) {
                //配置apk安装入口，查看源码的主配置可知
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                //文件作为数据源，且设置安装的类型
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

                //这个是正常开启另一activity的方式，下面的是通过隐式意图开启
                startActivity(intent);
            }

        }

    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "DownloadNerVersion";
            String channelName = "版本更新";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(this, "DownloadNerVersion");
            builder.setContentTitle(title)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pi);
            if (progress >= 0) {
                builder.setContentText(progress + "%");
                builder.setProgress(100, progress, false);
            }
        } else {
            builder = new NotificationCompat.Builder(this);
            builder.setContentTitle(title)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pi);
            if (progress >= 0) {
                builder.setContentText(progress + "%");
                builder.setProgress(100, progress, false);
            }
        }
        return builder.build();
    }
}
