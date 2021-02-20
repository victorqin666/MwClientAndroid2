package com.work.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Created by tangyx on 16/3/16.
 *
 */
public class DownloadService extends Service {
    public final static String URL="url";
    private boolean isDown;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isDown = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isDown){
            isDown=false;
            Intent downloadIntent = new Intent(this,DownloadActivity.class);
            downloadIntent.putExtra(URL,intent.getStringExtra(URL));
            downloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(downloadIntent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDown=false;
    }
    public static void intentDownloadService(Context context,String url){
        Intent intent = new Intent(context,DownloadService.class);
        intent.putExtra(URL,url);
        context.startService(intent);
    }
}
