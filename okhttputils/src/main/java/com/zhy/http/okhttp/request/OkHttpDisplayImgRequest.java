package com.zhy.http.okhttp.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zhy.http.okhttp.ImageUtils;
import com.zhy.http.okhttp.callback.ResultCallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by zhy on 15/11/6.
 */
public class OkHttpDisplayImgRequest extends OkHttpGetRequest
{
    private ImageView imageview;
    private int errorResId;

    protected OkHttpDisplayImgRequest(
            String url, String tag, Map<String,
            String> params, Map<String, String> headers,
            ImageView imageView, int errorResId)
    {
        super(url, tag, params, headers);
        this.imageview = imageView;
        this.errorResId = errorResId;
    }

    private void setErrorResId()
    {
        mOkHttpClientManager.getDelivery().post(new Runnable()
        {
            @Override
            public void run()
            {
                imageview.setImageResource(errorResId);
            }
        });
    }

    @Override
    public void invokeAsyn(final ResultCallback callback)
    {
        prepareInvoked(callback);

        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(final Request request, final IOException e)
            {
                setErrorResId();
                mOkHttpClientManager.
                        sendFailResultCallback(request, e, callback);

            }

            @Override
            public void onResponse(Response response)
            {
                InputStream is = null;
                try
                {
                    is = response.body().byteStream();
                    ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
                    ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(imageview);
                    int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
                    try
                    {
                        is.reset();
                    } catch (IOException e)
                    {
                        response = getInputStream();
                        is = response.body().byteStream();
                    }

                    BitmapFactory.Options ops = new BitmapFactory.Options();
                    ops.inJustDecodeBounds = false;
                    ops.inSampleSize = inSampleSize;
                    final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
                    mOkHttpClientManager.getDelivery().post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            imageview.setImageBitmap(bm);
                        }
                    });
                    mOkHttpClientManager.
                            sendSuccessResultCallback(request, callback);
                } catch (Exception e)
                {
                    setErrorResId();
                    mOkHttpClientManager.
                            sendFailResultCallback(request, e, callback);

                } finally
                {
                    try
                    {
                        if (is != null)
                        {
                            is.close();
                        }
                    } catch (IOException e)
                    {
                    }
                }
            }
        });


    }

    private Response getInputStream() throws IOException
    {
        Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    @Override
    public <T> T invoke(Class<T> clazz) throws IOException
    {
        return null;
    }
}
