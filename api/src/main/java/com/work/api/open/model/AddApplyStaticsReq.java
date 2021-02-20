package com.work.api.open.model;

/**
 * Created by tangyx
 * Date 2020/11/27
 * email tangyx@live.com
 */

public class AddApplyStaticsReq extends BaseReq {

    private int audioMinutes;
    private int videoMinutes;
    private String createType="android";

    public int getAudioMinutes() {
        return audioMinutes;
    }

    public void setAudioMinutes(int audioMinutes) {
        this.audioMinutes = audioMinutes;
    }

    public int getVideoMinutes() {
        return videoMinutes;
    }

    public void setVideoMinutes(int videoMinutes) {
        this.videoMinutes = videoMinutes;
    }

    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }
}