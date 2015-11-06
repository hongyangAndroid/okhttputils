package com.zhy.http.okhttp.request;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.zhy.http.okhttp.L;
import com.zhy.http.okhttp.callback.ResultCallback;

import java.util.Map;

/**
 * Created by zhy on 15/11/6.
 */
public class OkHttpPostRequest extends OkHttpRequest
{

    protected OkHttpPostRequest(String url, String tag, Map<String, String> params, Map<String, String> headers)
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
        Request.Builder builder = new Request.Builder();
        appendHeaders(builder,headers);
        builder.url(url).tag(tag).post(requestBody);
        return builder.build();
    }

    @Override
    protected RequestBody buildRequestBody()
    {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        addParams(builder, params);
        return builder.build();
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, final ResultCallback callback)
    {
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.Listener()
        {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength)
            {
                L.e("" + (bytesWritten * 1.0f / contentLength));
                callback.inProgress(bytesWritten * 1.0f / contentLength);
            }
        });
        return countingRequestBody;
    }

    private void addParams(FormEncodingBuilder builder, Map<String, String> params)
    {
        if (builder == null)
        {
            throw new IllegalArgumentException("builder can not be null .");
        }

        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                builder.add(key, params.get(key));
            }
        }
    }
}
