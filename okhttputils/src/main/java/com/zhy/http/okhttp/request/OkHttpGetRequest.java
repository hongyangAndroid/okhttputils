package com.zhy.http.okhttp.request;

import android.text.TextUtils;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.util.Map;

/**
 * Created by zhy on 15/11/6.
 */
public class OkHttpGetRequest extends OkHttpRequest
{
    protected OkHttpGetRequest(String url, String tag, Map<String, String> params, Map<String, String> headers)
    {
        super(url, tag, params, headers);
    }


    @Override
    protected Request buildRequest()
    {
        if (TextUtils.isEmpty(url))
        {
            throw new IllegalArgumentException("url can not be empty!");
        }
        //append params , if necessary
        url = appendParams(url, params);
        Request.Builder builder = new Request.Builder();
        //add headers , if necessary
        appendHeaders(builder,headers);
        builder.url(url).tag(tag);
        return builder.build();
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        return null;
    }


    private String appendParams(String url, Map<String, String> params)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(url + "?");
        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
