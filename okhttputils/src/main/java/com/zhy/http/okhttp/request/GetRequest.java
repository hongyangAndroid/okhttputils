package com.zhy.http.okhttp.request;

import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhy on 15/12/14.
 */
public class GetRequest extends OkHttpRequest
{
    private CacheControl cacheControl;

    public GetRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id, CacheControl cacheControl)
    {
        super(url, tag, params, headers,id);
        this.cacheControl = cacheControl;
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return null;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody)
    {
        if (null == cacheControl)
        {
            return builder.get().build();
        } else
        {
            return builder.get().cacheControl(cacheControl).build();
        }
    }


}
