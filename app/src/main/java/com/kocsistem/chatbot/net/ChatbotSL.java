package com.kocsistem.chatbot.net;

import android.content.Context;

import com.kocsistem.chatbot.model.ServiceErrorModel;
import com.kocsistem.chatbot.model.ServiceModel;
import com.kocsistem.chatbot.model.LoginRequestModel2;
import com.kocsistem.chatbot.model.UserInfo;
import com.ks.network.service.core.KSNetworkConfig;
import com.ks.network.service.core.KSNetworkManager;
import com.ks.network.service.listener.KSNetworkResponseListener;


/**
 * Created by ub on 19.6.2017 .
 */

public class ChatbotSL {

    private Context context;
    KSNetworkManager ksNetworkManager;
    public static final String RESULT_TAG = "ResultData";

    public ChatbotSL(Context mContex) {
        this.context = mContex;
        ksNetworkManager = new KSNetworkManager(context);
        ksNetworkManager.setNetworkException(new ChatbotNetworkException(context));
        KSNetworkConfig.getInstance().setURL("https://directline.botframework.com/v3/directline/");
        KSNetworkConfig.getInstance().getDefaultHeaders().put("Cache-Control", "no-cache");
        //KSNetworkConfig.getInstance().getDefaultHeaders().put("LanguageCode", "en");
       // KSNetworkConfig.getInstance().getDefaultHeaders().put("Authorization", "ac9e6da4-b355-e711-80e1-001dd8b7221c");
        //KSNetworkConfig.getInstance().getDefaultHeaders().put("ApiKey", "Zjg4MjEyN2EtYWM1OC00YzAxLTk5MDUtYTU2OTdhMjU0NmE0");
        //KSNetworkConfig.getInstance().getDefaultHeaders().put("LocalDate", "1497869200");

        for ( int i = 400; i<500;i++ )
        {
            KSNetworkConfig.getInstance().getResultErrorCode().add(i);
        }


    }

    public void getNotificationList(final KSNetworkResponseListener<ServiceModel, ServiceErrorModel> listener) {

        String url = "User/NotificationList";
        KSNetworkConfig.getInstance().getDefaultHeaders().remove("UserToken");
        //ksNetworkManager.getDataList(url, listener, NotificationInfo.class, ServiceModel.class, RESULT_TAG);

    }



    public void login(LoginRequestModel2 model, final KSNetworkResponseListener<ServiceModel, ServiceErrorModel> listener) {
        String url = "Security/Login";
        KSNetworkConfig.getInstance().getDefaultHeaders().put("UserToken", "96dfb14e-1890-417f-bd9c-bb1be76d3370");
        ksNetworkManager.postDataModel(url, model, listener, UserInfo.class, ServiceModel.class, RESULT_TAG);
    }

}
