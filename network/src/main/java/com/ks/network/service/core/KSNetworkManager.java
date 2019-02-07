package com.ks.network.service.core;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ks.network.service.exception.KSNetworkException;
import com.ks.network.service.listener.KSNetworkResponseListener;
import com.ks.network.service.model.ErrorModel;
import com.ks.network.service.model.ResultModel;
import com.ks.network.service.volley.CustomVolleyRequestQueue;
import com.ks.network.service.volley.GenericObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by gurkankesgin on 13.6.2017 .
 */

public class KSNetworkManager<TModel, TError> {

    Context context;
    RequestQueue requestQueue;

    KSNetworkException<TModel, TError> networkException;

    public void setNetworkException(KSNetworkException<TModel, TError> mNetworkException) {
        networkException = mNetworkException;
    }

    public KSNetworkManager(Context mContext) {
        this.context = mContext;
    }

    RequestQueue getVolley() {
        requestQueue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();
        return requestQueue;
    }


    public GenericObjectRequest getDataModel(final String method, final KSNetworkResponseListener<TModel, TError> listener, final Class<TModel> responseModelType, final Class<TError> errorModelType, final String jsonKey) {
        GenericObjectRequest req = new GenericObjectRequest(Request.Method.GET, KSNetworkConfig.getInstance().getURL() + method, null, String.class,
                responseModelType, new Response.Listener<ResultModel<TModel>>() {
            @Override
            public void onResponse(ResultModel<TModel> response) {

                if (networkException != null) {

                    response.requestParams.put("URL", KSNetworkConfig.getInstance().getURL() + method);
                    response.requestParams.put("Header", KSNetworkConfig.getInstance().getDefaultHeaders());

                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(response.getJson());
                    JsonElement jsonData = jsonElement.getAsJsonObject().get(jsonKey);

                    Gson gson = new Gson();
                    JsonObject jsonObject;
                    // verilen resultData parametresine göre data gelirse al ve modele ya da string e dönüştür
                    if (jsonData != null&&!jsonData.isJsonNull())
                        if (responseModelType.equals(Boolean.class)) {
                            Boolean value = gson.fromJson(jsonData, Boolean.class);
                            response.setChildObject(value);
                            showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders());

                        } else if (responseModelType.equals(String.class)) {
                            Boolean value = gson.fromJson(jsonData, Boolean.class);
                            response.setChildObject(value);
                            showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders());

                        }
                        else if (responseModelType.equals(Integer.class)) {
                            Integer value = gson.fromJson(jsonData, Integer.class);
                            response.setChildObject(value);
                            showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders());

                        }
                        else {
                            jsonObject = jsonData.getAsJsonObject();
                            Object entity = gson.fromJson(jsonObject, responseModelType);
                            response.setChildObject(entity);
                            showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders());

                        }

                    else {
                        if (responseModelType.equals(Boolean.class)) {
                            Boolean entity = false;
                            response.setChildObject(entity);
                            showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders());

                        } else if (responseModelType.equals(String.class)) {
                            String entity = null;
                            response.setChildObject(entity);
                            showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders());

                        } else if (responseModelType.equals(Integer.class)) {
                            Integer entity = null;
                            response.setChildObject(entity);
                            showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders());

                        } else {
                            jsonObject = new JsonObject();
                            Object entity = gson.fromJson(jsonObject, responseModelType);
                            response.setChildObject(entity);
                            showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders());


                        }  }



                    networkException.checkSuccessData(listener, response);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorModel<TError> errorModel = fillErrorModel(error, errorModelType);
                networkException.checkErrorData(listener,errorModel);
                showLog(errorModel,KSNetworkConfig.getInstance().getURL() + method,KSNetworkConfig.getInstance().getDefaultHeaders());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return KSNetworkConfig.getInstance().getDefaultHeaders();
            }

        };

        getVolley().add(req);

        return req;

    }

    private ErrorModel<TError> fillErrorModel(VolleyError error, Class<TError> errorModelType) {
        String body = null;
        ErrorModel<TError> errorModel = new ErrorModel<TError>();
        String statusCode = "-1";
        if (error.networkResponse != null) {


            statusCode = String.valueOf(error.networkResponse.statusCode);
            if (error.networkResponse.data != null) {
                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            // gelen hatayı baseerrormodele dönüştür ama 404 hatası html sayfası döndürüyor !!!
            try {
                Object entity = new Gson().fromJson(body, errorModelType);

                errorModel.setDescription(error.getMessage());
                errorModel.setErrorCode(statusCode);
                errorModel.setErrorModel((TError) entity);
            } catch (Exception e) {
                Object entity = new Gson().fromJson("", errorModelType);
                errorModel.setDescription(error.getMessage());
                errorModel.setErrorCode(statusCode);
                errorModel.setErrorModel((TError) entity);
            }

        } else {
            Object entity = new Gson().fromJson("", errorModelType);
            errorModel.setDescription(error.getMessage());
            errorModel.setErrorCode(statusCode);
            errorModel.setErrorModel((TError) entity);
        }


        return errorModel;
    }

    public GenericObjectRequest getDataList(final String method, final KSNetworkResponseListener<TModel, TError> listener, final Class<TModel> responseModelType, final Class<TError> errorModelType, final String jsonKey) {
        GenericObjectRequest req = new GenericObjectRequest(Request.Method.GET, KSNetworkConfig.getInstance().getURL() + method, null, String.class,
                responseModelType, new Response.Listener<ResultModel<TModel>>() {
            @Override
            public void onResponse(ResultModel<TModel> response) {

                if (networkException != null) {


                    response.requestParams.put("URL", KSNetworkConfig.getInstance().getURL() + method);
                    response.requestParams.put("Header", KSNetworkConfig.getInstance().getDefaultHeaders());

                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(response.getJson());
                    JsonElement jsonObject = jsonElement.getAsJsonObject().get(jsonKey);
                    List<Object> lst = new ArrayList<Object>();
                    if (jsonObject != null) {
                        JsonArray array = jsonObject.getAsJsonArray();

                        ResultModel<TModel> model = response;
                        Gson gson = new Gson();

                        for (final JsonElement jsn : array) {
                            Object entity = gson.fromJson(jsn, responseModelType);
                            lst.add(entity);
                        }
                    }

                    response.setChildOjectList(lst);

                    networkException.checkSuccessDataList(listener, response);
                    showLog(jsonElement,KSNetworkConfig.getInstance().getURL() + method,KSNetworkConfig.getInstance().getDefaultHeaders());

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorModel<TError> errorModel = fillErrorModel(error, errorModelType);
                networkException.checkErrorData(listener,errorModel);
                showLog(errorModel,KSNetworkConfig.getInstance().getURL() + method,KSNetworkConfig.getInstance().getDefaultHeaders());


            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return KSNetworkConfig.getInstance().getDefaultHeaders();
            }

        };

        getVolley().add(req);

        return req;
    }

    public void postDataModel(final String method, final TModel requestModel, final KSNetworkResponseListener<TModel, TError> listener, final Class<TModel> responseModelType, final Class<TError> errorModelType, final String jsonKey) {

        JSONObject request = null;
        Gson gson = new Gson();
        String jsonString = gson.toJson(requestModel);
        try {
            request = new JSONObject(jsonString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                KSNetworkConfig.getInstance().getURL() + method, request,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        Gson gson = new Gson();
                        ResultModel<TModel> resultModel = new ResultModel<>();

                        resultModel.setJson(response.toString());
                        if (networkException != null) {


                            resultModel.requestParams.put("URL", KSNetworkConfig.getInstance().getURL() + method);
                            resultModel.requestParams.put("Header", KSNetworkConfig.getInstance().getDefaultHeaders());
                            resultModel.requestParams.put("RequestModel", requestModel);

                            JsonParser jsonParser = new JsonParser();
                            JsonElement jsonElement = jsonParser.parse(resultModel.getJson());
                            JsonElement jsonData = jsonElement.getAsJsonObject().get(jsonKey);
                            JsonObject jsonObject;
                            // verilen resultData parametresine göre data gelirse al ve modele ya da string e dönüştür
                            if (jsonData != null&&!jsonData.isJsonNull())
                                if (responseModelType.equals(Boolean.class)) {
                                    Boolean entity = gson.fromJson(jsonData, Boolean.class);
                                    resultModel.setChildObject(entity);
                                    showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders(), requestModel);

                                } else if (responseModelType.equals(String.class)) {
                                    String entity = gson.fromJson(jsonData, String.class);
                                    resultModel.setChildObject(entity);
                                    showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders(), requestModel);

                                }else if(responseModelType.equals(Integer.class)){
                                    Integer entity = gson.fromJson(jsonData, Integer.class);
                                    resultModel.setChildObject(entity);
                                    showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders(), requestModel);

                                } else {
                                    jsonObject = jsonData.getAsJsonObject();
                                    Object entity = gson.fromJson(jsonObject, responseModelType);
                                    resultModel.setChildObject(entity);
                                    showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders(), requestModel);

                                }
                            else {
                                if (responseModelType.equals(Boolean.class)) {
                                    Boolean entity = false;
                                    resultModel.setChildObject(entity);
                                    showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders(), requestModel);

                                } else if (responseModelType.equals(String.class)) {
                                    String entity = null;
                                    resultModel.setChildObject(entity);
                                    showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders(), requestModel);

                                } else if (responseModelType.equals(Integer.class)) {
                                    Integer entity = null;
                                    resultModel.setChildObject(entity);
                                    showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders(), requestModel);

                                } else {
                                    jsonObject = new JsonObject();
                                    Object entity = gson.fromJson(jsonObject, responseModelType);
                                    resultModel.setChildObject(entity);
                                    showLog(jsonElement, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders(), requestModel);

                                } }
                            networkException.checkSuccessData(listener, resultModel);

                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorModel<TError> errorModel = fillErrorModel(error, errorModelType);
                networkException.checkErrorData(listener, errorModel);
                showLog(errorModel, KSNetworkConfig.getInstance().getURL() + method, KSNetworkConfig.getInstance().getDefaultHeaders(),requestModel);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return KSNetworkConfig.getInstance().getDefaultHeaders();
            }

        };

        getVolley().add(jsonObjReq);

    }

    public void postDataModelList(final String method, final Class<TModel> responseModelType, final TModel requestModel, final KSNetworkResponseListener<TModel, TError> listener, final Class<TError> errorModelType, final String jsonKey) {

        JSONObject request = null;
        Gson gson = new Gson();
        String jsonString = gson.toJson(requestModel);
        try {
            request = new JSONObject(jsonString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                KSNetworkConfig.getInstance().getURL() + method, request,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        Gson gson = new Gson();
                        ResultModel<TModel> resultModel = new ResultModel<>();

                        resultModel.setJson(response.toString());
                        if (networkException != null) {


                            resultModel.requestParams.put("URL", KSNetworkConfig.getInstance().getURL() + method);
                            resultModel.requestParams.put("Header", KSNetworkConfig.getInstance().getDefaultHeaders());
                            resultModel.requestParams.put("RequestModel", requestModel);

                            JsonParser jsonParser = new JsonParser();
                            JsonElement jsonElement = jsonParser.parse(resultModel.getJson());
                            JsonElement jsonObject = jsonElement.getAsJsonObject().get(jsonKey);
                            List<Object> lst = new ArrayList<Object>();
                            if (jsonObject != null) {

                                JsonArray array = jsonObject.getAsJsonArray();

                                if (array != null) {
                                    array = jsonObject.getAsJsonArray();

                                    for (final JsonElement jsn : array) {
                                        Object entity = gson.fromJson(jsn, responseModelType);
                                        lst.add(entity);
                                    }
                                }
                            }
                            resultModel.setChildOjectList(lst);

                            networkException.checkSuccessDataList(listener, resultModel);

                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorModel<TError> errorModel = fillErrorModel(error, errorModelType);
                networkException.checkErrorData(listener, errorModel);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return KSNetworkConfig.getInstance().getDefaultHeaders();
            }

        };

        getVolley().add(jsonObjReq);

    }

    public StringRequest getBasic(String method, final KSNetworkResponseListener<TModel, TError> listener) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, KSNetworkConfig.getInstance().getURL() + method,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ResultModel<TModel> resultModel = new ResultModel<>();
                        resultModel.setModel((TModel) response);
                        listener.onSuccess(resultModel);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorModel<TError> errorModel = new ErrorModel<TError>();
                errorModel.setDescription("");
                errorModel.setErrorCode(error.networkResponse.statusCode + "");
                listener.onError(errorModel);
            }
        });

        getVolley().add(stringRequest);

        return stringRequest;
    }





    // Loglari görebilmek için logcat search e "ServiceResponse" yaz ve filter ı debug a cek

    private void showLog(Object entity, String s, Object responseModelType) {
        Log.d("ServiceResponse","*********************************************************ServiceResponse Begin*********************************************************");
        Log.d("ServiceResponse", " Method = "+ s);
        Log.d("ServiceResponse", " Type = "+ "Get");
        Log.d("ServiceResponse", " ResponseModel = "+ entity.toString());
        try {
            JSONObject jsonObject = new JSONObject(entity.toString());
            Log.d("ServiceResponse", " ResponseModelJson = "+ jsonObject.toString());
        } catch (JSONException e) {
        }
        Log.d("ServiceResponse", " HeadersJson = "+ responseModelType.toString() + "\n\n");
        try {
            JSONObject jsonObject = new JSONObject(responseModelType.toString());
            Log.d("ServiceResponse", " HeaderJson = "+ jsonObject.toString());
        } catch (JSONException e) {
        }
        Log.d("ServiceResponse","*********************************************************ServiceResponse End*********************************************************");
    }

    private void showLog(Object entity, String s, Object responseModelType, TModel requestModel) {
        Log.d("ServiceResponse","*********************************************************ServiceResponse Begin*********************************************************");
        Log.d("ServiceResponse", " Method = "+ s);
        Log.d("ServiceResponse", " Type = "+ "Post");
        Log.d("ServiceResponse", " ResponseModel = "+ entity.toString());
        try {
            JSONObject jsonObject = new JSONObject(entity.toString());
            Log.d("ServiceResponse", " ResponseModelJson = "+ jsonObject.toString());
        } catch (JSONException e) {
        }
        Log.d("ServiceResponse", " Headers = "+ responseModelType.toString());
        try {
            JSONObject jsonObject = new JSONObject(responseModelType.toString());
            Log.d("ServiceResponse", " HeaderJson = "+ jsonObject.toString());
        } catch (JSONException e) {
        }
        Log.d("ServiceResponse", " Request = "+ requestModel.toString());
        try {
            JSONObject jsonObject = new JSONObject(requestModel.toString());
            Log.d("ServiceResponse", " RequestJson = "+ jsonObject.toString());
        } catch (JSONException e) {
        }
        Log.d("ServiceResponse","\n\n*********************************************************ServiceResponse End*********************************************************");
    }


}
