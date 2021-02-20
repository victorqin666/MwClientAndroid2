package com.work.api.open.model.client;

import com.http.network.model.ClientModel;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/27
 * email tangyx@live.com
 */

public class OpenWork extends ClientModel {
    private String toolCategory;
    private List<OpenData> toolDataList;

    public String getToolCategory() {
        return toolCategory;
    }

    public void setToolCategory(String toolCategory) {
        this.toolCategory = toolCategory;
    }

    public List<OpenData> getToolDataList() {
        return toolDataList;
    }

    public void setToolDataList(List<OpenData> toolDataList) {
        this.toolDataList = toolDataList;
    }
}
