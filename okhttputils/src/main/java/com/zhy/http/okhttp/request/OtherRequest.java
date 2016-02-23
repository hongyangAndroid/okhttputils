package com.zhy.http.okhttp.request;

import com.zhy.http.okhttp.OkHttpUtils;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhy on 16/2/23.
 */
public class OtherRequest extends OkHttpRequest
{
    private RequestBody requestBody;
    private String method;

    public OtherRequest(RequestBody requestBody, String method, String url, Object tag, Map<String, String> params, Map<String, String> headers)
    {
        super(url, tag, params, headers);
        this.requestBody = requestBody;
        this.method = method;
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return requestBody;
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody)
    {
        if (method.equals(OkHttpUtils.METHOD.PUT))
        {
            builder.put(requestBody);
        } else if (method.equals(OkHttpUtils.METHOD.DELETE))
        {
            if (requestBody == null)
                builder.delete();
            else
                builder.delete(requestBody);
        } else if (method.equals(OkHttpUtils.METHOD.HEAD))
        {
            builder.head();
        } else if (method.equals(OkHttpUtils.METHOD.PATCH))
        {
            builder.patch(requestBody);
        }

        return builder.build();
    }
}
