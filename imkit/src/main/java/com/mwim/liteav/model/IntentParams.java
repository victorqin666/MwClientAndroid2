package com.mwim.liteav.model;

import com.mwim.liteav.login.UserModel;

import java.io.Serializable;
import java.util.List;

public class IntentParams implements Serializable {
    public List<UserModel> mUserModels;

    public IntentParams(List<UserModel> userModels) {
        mUserModels = userModels;
    }
}