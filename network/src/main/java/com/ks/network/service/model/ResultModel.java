package com.ks.network.service.model;

import com.android.volley.NetworkResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gurkankesgin on 13.6.2017 .
 */

public class ResultModel<TModel> {

    private TModel model;
    private List<TModel> list;

    private String json;
    private NetworkResponse networkResponse;
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public NetworkResponse getNetworkResponse() {
        return networkResponse;
    }

    public void setNetworkResponse(NetworkResponse networkResponse) {
        this.networkResponse = networkResponse;
    }

    public TModel getModel() {
        return model;
    }

    public void setModel(TModel model) {
        this.model = model;
    }

    public List<TModel> getList() {
        return list;
    }

    public void setList(List<TModel> list) {
        this.list = list;
    }

    private List childObjectList;

    public List getChildOjectList() {
        return childObjectList;
    }

    public void setChildOjectList(List arrayObject) {
        this.childObjectList = arrayObject;
    }

    private  Object childObject;

    public Object getChildObject() {
        return childObject;
    }

    public void setChildObject(Object childObject) {
        this.childObject = childObject;
    }


    public Map<String,Object> requestParams = new HashMap<>();

    public List getChildObjectList() {
        return childObjectList;
    }

    public void setChildObjectList(List childObjectList) {
        this.childObjectList = childObjectList;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }
}
