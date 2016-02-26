package com.zhy.http.okhttp.builder;

import com.zhy.http.okhttp.request.RequestCall;

import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class OkHttpRequestBuilder<T>
{
    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected Map<String, String> params;

    public abstract T url(String url);

    public abstract T tag(Object tag);

    public abstract T params(Map<String, String> params);

    public abstract T addParams(String key, String val);

    public abstract T headers(Map<String, String> headers);

    public abstract T addHeader(String key, String val);

    public abstract RequestCall build();


}
