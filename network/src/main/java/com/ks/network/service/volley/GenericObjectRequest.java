package com.ks.network.service.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ks.network.service.model.ResultModel;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gurkankesgin on 13.6.2017 .
 */

public class GenericObjectRequest<R, T> extends Request<T> {

    private final Gson gson = new Gson();
    private Response.Listener<T> listener;
    private Class<R> inputType;
    private Class<T> outputType;
    private R object;

    private static final String PROTOCOL_CHARSET = "utf-8";

    public GenericObjectRequest(int method, String url, R postObject, Class<R> inputType, Class<T> outputType,
                                Response.Listener<T> listener,
                                Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.inputType = inputType;
        this.outputType = outputType;
        this.object = postObject;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();

        //Optional headers
      /*  headers.put("Accept", ApplicationConstant.ACCEPT);
        headers.put("Content-Type", ApplicationConstant.CONTENT_TYPE);

        if (GlobalData.getInstance().getAuthToken()!=null){
            headers.put("Authorization", GlobalData.getInstance().getAuthToken());
        }*/
        return headers;
    }

    @Override
    public byte[] getBody() {
        String jsonString = gson.toJson(object, inputType);
        try {
            return jsonString.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", jsonString, PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            ResultModel<T> tCustomModel = new ResultModel<>();

            tCustomModel.setModel(gson.fromJson(json, outputType));
            tCustomModel.setJson(json);
            tCustomModel.setNetworkResponse(response);

            return (Response<T>) Response.success(tCustomModel, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));

        }
    }

}