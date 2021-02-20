package com.work.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.FileProvider;

import com.work.util.ToastUtil;

import java.io.File;


public class UpdateController implements DownloadListener {

    private static final int NOTIFY_ID = 10000;

    private static final String ACTION_CANCEL_DOWNLOAD = ".cancelDownloadApk";
    private Notification.Builder mNotification = null;
    private NotificationManager mNotifyManager = null;
    private DownloadTask mDownloadTask;

    private Context mContext;
    private String mActionCancel;
    private String mApkPath;

    private int mIcon;
    private CancelBroadcastReceiver mReceiver;

    /**
     * 弹出框下载
     */
    private Handler mHandler;

    UpdateController() {

    }

    /**
     * @param context
     */
    void init(final Context context) {
        if (context == null) {
            throw new IllegalArgumentException("How context can be null?");
        }
        mHandler = new Handler(context.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        mContext = context;
        mIcon = R.drawable.download_logo;
        PackageManager manager = context.getPackageManager();
        PackageInfo mPackageInfo;
        try {
            mPackageInfo = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Can not find package information");
        }
        String mPackageName = mPackageInfo.packageName;
        mActionCancel = mPackageName + ACTION_CANCEL_DOWNLOAD;

        mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelId = "APP更新";
            NotificationChannel channel = new NotificationChannel(channelId,channelId,NotificationManager.IMPORTANCE_LOW);
            mNotifyManager.createNotificationChannel(channel);
            mNotification = new Notification.Builder(mContext,channelId);
            mNotification.setTicker(channelId);
        }else{
            mNotification = new Notification.Builder(mContext);
        }
        mNotification
                .setSmallIcon(mIcon)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
    }

    private void notifyDownloadFinish() {
        Intent openIntent = getOpenIntent();
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, openIntent, 0);
        mNotification.setContentIntent(contentIntent);
        mNotification.setContentTitle(mContext.getResources().getString(R.string.download_finish_title));
        mNotification.setProgress(100,100,false);
        mNotifyManager.notify(NOTIFY_ID, mNotification.build());
//        ToastUtil.success(mContext, R.string.download_finish_ticker_text);
        openFile();
    }

    private void notifyDownLoadStart() {
        ToastUtil.info(mContext, R.string.download_ready);
        mNotification.setSmallIcon(mIcon);
        mNotification.setContentTitle(mContext.getResources().getString(R.string.download_loading_start));
        mNotification.setProgress(100,0,true);
        mNotifyManager.notify(NOTIFY_ID, mNotification.build());
    }

    private void notifyDownLoading(int updatePercent) {
        Intent intent = new Intent(mActionCancel);
        PendingIntent contentIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        mNotification.setProgress(100,updatePercent,false);
        mNotification.setContentTitle(mContext.getResources().getString(R.string.download_loading_progress,updatePercent));
        mNotification.setContentIntent(contentIntent);
        mNotifyManager.notify(NOTIFY_ID, mNotification.build());
        if(mContext instanceof DownloadActivity){
            mHandler.sendEmptyMessage(updatePercent);
        }
    }

    public void beginDownLoad(String url) {
        beginDownLoad(url, false);
    }

    void beginDownLoad(String url, boolean forceReDownload) {
        String dir = mContext.getExternalFilesDir(null).getAbsolutePath();
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        int lastNameDex = fileName.indexOf("?");
        if(lastNameDex!=-1){//防止url后面携带参数
            fileName = fileName.substring(0,lastNameDex);
        }
        mApkPath = dir + File.separator + "Download" + File.separator + fileName;
        mDownloadTask = new DownloadTask(this, url, mApkPath);
        notifyDownLoadStart();
        if (forceReDownload) {
            deleteApkFile();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(mActionCancel);
        mReceiver = new CancelBroadcastReceiver();
        mContext.registerReceiver(mReceiver, filter);

        Thread thread = new Thread(mDownloadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void openFile() {
        Intent intent = getOpenIntent();
        mContext.startActivity(intent);
//        mNotifyManager.cancel(NOTIFY_ID);
        dismissDialog();
    }

    private Intent getOpenIntent() {
        Intent openIntent = new Intent(Intent.ACTION_VIEW);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            openIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileProvider", new File(mApkPath));
            openIntent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            openIntent.setDataAndType(Uri.fromFile(new File(mApkPath)), "application/vnd.android.package-archive");
        }
        return openIntent;
    }

    private void dismissDialog(){
        mContext.stopService(new Intent(mContext, DownloadService.class));
    }

    @Override
    public void onCancel() {
        mContext.stopService(new Intent(mContext, DownloadService.class));
        dismissDialog();
    }

    @Override
    public void onDone(boolean canceled, int result) {
        mContext.stopService(new Intent(mContext, DownloadService.class));
        if (canceled) {
            return;
        }
        switch (result) {
            case DownloadTask.RESULT_OK:
                try {
                    mContext.unregisterReceiver(mReceiver);
                }catch (Exception e){
                    e.printStackTrace();
                }
                mNotifyManager.cancelAll();
                notifyDownloadFinish();
                dismissDialog();
                break;
            case DownloadTask.RESULT_DOWNLOAD_ERROR:
                mContext.unregisterReceiver(mReceiver);
                mNotifyManager.cancel(NOTIFY_ID);
                deleteApkFile();
                ToastUtil.error(mContext, R.string.download_fail);
                dismissDialog();
                break;
            case DownloadTask.RESULT_NO_ENOUGH_SPACE:
                break;
            case DownloadTask.RESULT_URL_ERROR:
                mContext.unregisterReceiver(mReceiver);
                mNotifyManager.cancel(NOTIFY_ID);
                deleteApkFile();
                ToastUtil.error(mContext, R.string.download_error_url);
                dismissDialog();
                break;
        }
    }

    @Override
    public void onPercentUpdate(int percent) {
        notifyDownLoading(percent);
    }

    private void deleteApkFile() {
        File file = new File(mApkPath);
        if(file.exists()){
            file.delete();
        }
    }

    private class CancelBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            cancelUpdate();
        }
    }
    private void cancelUpdate(){
        mNotifyManager.cancel(NOTIFY_ID);
        mDownloadTask.cancel();
        try {
            mContext.unregisterReceiver(mReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
