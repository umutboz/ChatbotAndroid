package com.ks.network.service.listener;


import com.ks.network.service.model.ErrorModel;
import com.ks.network.service.model.ResultModel;

/**
 * Created by gurkankesgin on 13.6.2017 .
 */

public interface KSNetworkResponseListener<TModel,TError> {
    public void onSuccess(ResultModel<TModel> model);
    public void onError(ErrorModel<TError> errorModel);
}
