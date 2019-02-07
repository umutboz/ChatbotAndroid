package com.kocsistem.chatbot.model;

/**
 * Created by umut on 19.6.2017 .
 */


public class ServiceErrorModel {
    private String ResultMessage;
    private int ResultCode;
    private ResultData resultData;


    public ResultData getResultData() {
        return resultData;
    }

    public void setResultData(ResultData resultData) {
        this.resultData = resultData;
    }

    public String getMessage() {
        return ResultMessage;
    }

    public void setMessage(String errorMessage) {
        ResultMessage = errorMessage;
    }


    public int getCode() {
        return ResultCode;
    }

    public void setCode(int code) {
        this.ResultCode = code;
    }


    public class ResultData {

        private String NewToken;

        public String getNewToken() {
            return NewToken;
        }

        public void setNewToken(String newToken) {
            NewToken = newToken;
        }
    }
}
