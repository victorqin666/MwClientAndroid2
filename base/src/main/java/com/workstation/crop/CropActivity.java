package com.workstation.crop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.work.util.ToastUtil;
import com.workstation.android.BaseHomeActivity;
import com.workstation.android.R;
import com.workstation.android.TakePhotoActivity;
import com.workstation.crop.config.CropIwaSaveConfig;
import com.workstation.crop.config.CropProperty;
import com.workstation.crop.shape.CropIwaOvalShape;
import com.workstation.crop.shape.CropIwaRectShape;
import com.workstation.view.MaterialMenuDrawable;
import com.workstation.view.MaterialMenuView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CropActivity extends BaseHomeActivity {

    public static final String EXTRA_URI = "crop_image_uri";
    public static final String EXTRA_CROP_IWA_CONFIG = "crop_iwa_config";

    public static Intent callingIntent(Context context, Uri imageUri, CropProperty cropProperty) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_URI, imageUri);
        intent.putExtra(EXTRA_CROP_IWA_CONFIG,cropProperty);
        return intent;
    }

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        final Uri imageUri = getIntent().getParcelableExtra(EXTRA_URI);
        final CropProperty property = getIntent().getParcelableExtra(EXTRA_CROP_IWA_CONFIG);
        final CropIwaView mCropView = findViewById(R.id.crop_iwa_view);
        mCropView.setImageUri(imageUri);
        mCropView.configureOverlay()
                .setAspectRatio(property.getAspectRatio())
                .setCropShape(property.getCropShape().equals(CropProperty.OVAL)?new CropIwaOvalShape(mCropView.configureOverlay()):new CropIwaRectShape(mCropView.configureOverlay()))
                .setDynamicCrop(property.isDynamicOverlay())
                .apply();
        MaterialMenuView close = findViewById(R.id.close);
        close.setState(MaterialMenuDrawable.IconState.X);
        final MaterialMenuView complete = findViewById(R.id.complete);
        complete.setState(MaterialMenuDrawable.IconState.CHECK);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mCropView.setErrorListener(new CropIwaView.ErrorListener() {
            @Override
            public void onError(Throwable e) {
                complete.setEnabled(true);
                ToastUtil.warning(CropActivity.this,R.string.saving_image_error);
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete.setEnabled(false);
                ToastUtil.info(CropActivity.this,R.string.saving_image, Toast.LENGTH_LONG);
                mCropView.crop(new CropIwaSaveConfig.Builder(createNewEmptyFile())
                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                        .setQuality(100)
                        .build());
            }
        });
        mCropView.setCropSaveCompleteListener(new CropIwaView.CropSaveCompleteListener() {
            @Override
            public void onCroppedRegionSaved(Uri bitmapUri) {
                complete.setEnabled(true);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_URI,bitmapUri);
                intent.putExtra(EXTRA_CROP_IWA_CONFIG,property);
                setResult(TakePhotoActivity.RESULT_CROP_PHOTO,intent);
                finish();
            }
        });
        if(property.isDynamicAspectRation()){
            List<AspectRatio> list = Arrays.asList(new AspectRatio(3, 2),
                    new AspectRatio(4, 3),
                    new AspectRatio(5, 4),
                    new AspectRatio(1, 1),
                    new AspectRatio(4, 5),
                    new AspectRatio(3, 4),
                    new AspectRatio(2, 3));
            final AspectRatioPreviewAdapter mAdapter = new AspectRatioPreviewAdapter(list);
            mAdapter.setSelectAspectRation(list.get(3));
            RecyclerView mRecycler = findViewById(R.id.aspect_view);
            mRecycler.setVisibility(View.VISIBLE);
            mRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
            mRecycler.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    AspectRatio ratio = mAdapter.getItem(position);
                    if(ratio!=null && ratio!=mAdapter.getSelectAspectRation()){
                        mAdapter.setSelectAspectRation(ratio);
                        property.setAspectRatio(ratio);
                        mAdapter.notifyDataSetChanged();
                        mCropView.configureOverlay().setAspectRatio(ratio).apply();
                    }
                }
            });
        }
    }

    public Uri createNewEmptyFile() {
        return Uri.fromFile(new File(this.getApplication().getFilesDir(), System.currentTimeMillis() + ".png"));
    }

    @Override
    public boolean isShowTitleBar() {
        return false;
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this,0,null);
    }
}
