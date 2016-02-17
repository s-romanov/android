package com.psy.places.webservice.request;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.psy.places.webservice.PlacesApiConfig;
import com.psy.places.webservice.tool.GsonUtil;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Created by sromanov on 16/02/2016.
 */
public abstract class BaseJsonRequest<T> extends JsonRequest<T> {

    private static final String TAG = BaseJsonRequest.class.getSimpleName();

    protected abstract Type getResultType();

    private Gson mGson;

    protected BaseJsonRequest(int method, @NonNull String[] paths, @Nullable Map<String, String> queryParams, @Nullable JsonElement body,
                              Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(method, createUrl(paths, queryParams), createBody(body), listener, errorListener);
    }

    protected BaseJsonRequest(int method, String url, String requestBody, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);

        mGson = new Gson();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            if (TextUtils.isEmpty(jsonString)) {
                jsonString = "{}";
            }

            T result = GsonUtil.jsonToObject(mGson, jsonString, getResultType());
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        super.deliverResponse(response);
    }

    private static String createBody(JsonElement body) {
        if (body == null) {
            return null;
        }

        return body.isJsonNull() ? "" : body.toString();
    }

    private static final String createUrl(String[] paths, Map<String, String> queryParams) {
        Uri.Builder builder = Uri.parse(PlacesApiConfig.BASE_URL).buildUpon();
        for (String path : paths) {
            builder.appendEncodedPath(path);
        }
        if (queryParams != null) {
            Set<String> keys = queryParams.keySet();
            for (String key : keys) {
                builder.appendQueryParameter(key, queryParams.get(key));
            }
        }

        return builder.build().toString();
    }
}