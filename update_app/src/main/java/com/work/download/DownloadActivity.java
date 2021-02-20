package com.work.download;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author tangyx
 */

public class DownloadActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mUrl = getIntent().getStringExtra(DownloadService.URL);
        UpdateController mController = new UpdateController();
        mController.init(this);
        mController.beginDownLoad(mUrl, true);
        finish();
    }
}
