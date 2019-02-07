package com.kocsistem.chatbot.net;

import android.content.Context;

import com.google.gson.Gson;

import com.kocsistem.chatbot.model.ServiceErrorModel;
import com.kocsistem.chatbot.model.ServiceModel;
import com.ks.network.service.core.KSNetworkConfig;
import com.ks.network.service.exception.KSNetworkException;
import com.ks.network.service.listener.KSNetworkResponseListener;
import com.ks.network.service.model.ErrorModel;
import com.ks.network.service.model.ResultModel;


/**
 * Created by ub on 16.6.2017 .
 */

public class ChatbotNetworkException extends KSNetworkException<ServiceModel, ServiceErrorModel> {

    private Context context;

    public ChatbotNetworkException(Context context) {
        this.context=context;
    }

    @Override
    public void checkSuccessData(KSNetworkResponseListener<ServiceModel, ServiceErrorModel> listener, ResultModel<ServiceModel> resultModel) {


        //servisin kendi hata senaryosunu öğret

        ServiceModel ServiceModel = new Gson().fromJson(resultModel.getJson(), ServiceModel.class);

        if (ServiceModel.getResultCode() != 0) {

            ErrorModel<ServiceErrorModel> errorModelG = new ErrorModel<>();
            ServiceErrorModel errorModel = new ServiceErrorModel();
            errorModel.setCode(ServiceModel.getResultCode());
            errorModel.setMessage(ServiceModel.getResultMessage());

            errorModelG.setErrorModel(errorModel);
            listener.onError(errorModelG);

        } else {
            listener.onSuccess(resultModel);
        }


    }

    @Override
    public void checkSuccessDataList(KSNetworkResponseListener<ServiceModel, ServiceErrorModel> listener, ResultModel<ServiceModel> resultModel) {

        ServiceModel ServiceModel = new Gson().fromJson(resultModel.getJson(), ServiceModel.class);

        if (ServiceModel.getResultCode() != 0) {

            ErrorModel<ServiceErrorModel> errorModelG = new ErrorModel<>();
            ServiceErrorModel errorModel = new ServiceErrorModel();
            errorModel.setCode(ServiceModel.getResultCode());
            errorModel.setMessage(ServiceModel.getResultMessage());

            errorModelG.setErrorModel(errorModel);
            listener.onError(errorModelG);

        } else {

            listener.onSuccess(resultModel);
        }

    }

    // 200 gelmeyen durum -- verdiğin özel http hata kodlarına göre dönen model
    @Override
    public void checkErrorData(KSNetworkResponseListener<ServiceModel, ServiceErrorModel> listener, ErrorModel<ServiceErrorModel> errorModel) {

        for (Integer serviceErrorCode : KSNetworkConfig.getInstance().getResultErrorCode()) {
            if (Integer.parseInt(errorModel.getErrorCode()) == serviceErrorCode) {

            }
        }

        listener.onError(errorModel);

    }
}

