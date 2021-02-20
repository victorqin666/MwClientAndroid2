package com.mwim.qcloud.tim.uikit.business.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseFragment;

public class ProfileFragment extends BaseFragment {

    private ProfileLayout mProfileLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mBaseView = inflater.inflate(R.layout.profile_fragment, container, false);
        mProfileLayout = mBaseView.findViewById(R.id.profile_view);
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProfileLayout.updateProfile();
    }
}
