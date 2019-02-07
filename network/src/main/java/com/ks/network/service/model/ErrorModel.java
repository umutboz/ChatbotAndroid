package com.ks.network.service.model;

/**
 * Created by gurkankesgin on 13.6.2017 .
 */

public class ErrorModel<TError> {

    private TError errorModel;
    private String ErrorCode;
    private String description;

    public TError getErrorModel() {
        return errorModel;
    }

    public void setErrorModel(TError errorModel) {
        this.errorModel = errorModel;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        this.ErrorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
