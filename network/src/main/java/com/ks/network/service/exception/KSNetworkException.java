package com.ks.network.service.exception;


import com.ks.network.service.listener.KSNetworkResponseListener;
import com.ks.network.service.model.ErrorModel;
import com.ks.network.service.model.ResultModel;

public abstract class KSNetworkException<TModel,TError> {

    public abstract void checkSuccessData(KSNetworkResponseListener<TModel, TError> listener, ResultModel<TModel> responseModel);

    public abstract void checkSuccessDataList(KSNetworkResponseListener<TModel, TError> listener, ResultModel<TModel> responseModel) ;

    public abstract void checkErrorData(KSNetworkResponseListener<TModel, TError> listener, ErrorModel<TError> responseModel) ;
}
