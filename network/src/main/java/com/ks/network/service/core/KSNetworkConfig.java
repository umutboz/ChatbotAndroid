package com.ks.network.service.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gurkankesgin on 15.6.2017 .
 */
public class KSNetworkConfig {

    private static KSNetworkConfig intance = new KSNetworkConfig();
    private String URL;
    private Map<String, String> defaultHeaders = new HashMap<>();

    private List<Integer> resultErrorCode = new ArrayList<>();



    public static KSNetworkConfig getInstance() {
        return intance;
    }

    private KSNetworkConfig() {
    }


    public void deleteAllHeaders() {
        defaultHeaders = new HashMap<>();
    }

    public Map<String, String> getDefaultHeaders() {
        return defaultHeaders;
    }

    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public List<Integer> getResultErrorCode() {
        return resultErrorCode;
    }

    public void setResultErrorCode(List<Integer> resultErrorCode) {
        this.resultErrorCode = resultErrorCode;
    }
}
