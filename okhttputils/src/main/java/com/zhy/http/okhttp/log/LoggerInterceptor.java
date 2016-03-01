package com.zhy.http.okhttp.log;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by zhy on 16/3/1.
 */
public class LoggerInterceptor implements Interceptor
{
    public static final String TAG = "OkHttpUtils";
    private String tag;

    public LoggerInterceptor(String tag)
    {
        if (TextUtils.isEmpty(tag))
        {
            tag = TAG;
        }

        this.tag = tag;
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        String url = request.url().toString();
        Headers headers = request.headers();

        Log.e(tag, "========OkHttpUtils'log=======");
        Log.e(tag, "method : " + request.method());
        Log.e(tag, "url : " + url);
        if (headers != null && headers.size() > 0)
        {
            Log.e(tag, "headers : " + headers.toString());
        }
        RequestBody requestBody = request.body();
        if (requestBody != null)
        {
            Log.e(tag, "requestBody's contentType : " + requestBody.contentType().toString());
            if (isText(requestBody.contentType()))
            {
                Log.e(tag, "requestBody's content : " + bodyToString(request));
            } else
            {
                Log.e(tag, "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
            }
        }

        Response response = chain.proceed(request);
        return response;
    }

    private boolean isText(MediaType mediaType)
    {
        if (mediaType.type().equals("text") ||
                mediaType.subtype().equals("json") ||
                mediaType.subtype().equals("xml") ||
                mediaType.subtype().equals("html") ||
                mediaType.subtype().equals("webviewhtml")
                )
            return true;
        return false;
    }

    private String bodyToString(final Request request)
    {
        try
        {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e)
        {
            return "something error when show requestBody.";
        }
    }
}
