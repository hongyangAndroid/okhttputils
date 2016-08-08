package com.zhy.http.okhttp.callback;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Git@Smark on 2016/7/17.
 * 简化接口，支持返回String、Array、Entity、List<T>、BaseEntity<T>等等
 */
public abstract class XHttpCallBack<T> extends Callback<T> {

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String result = unBunding(response.body().string());
        if (result == null){
            return null;
        }
        if (getClass().getGenericSuperclass() == XHttpCallBack.class){
            return (T)result;// 默认返回String
        }
        if(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] == String.class){
            return (T)result;// String类型直接返回
        }
        Type type = $Gson$Types.canonicalize(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        return new Gson().fromJson(result, type);
    }

    //注意：非UI线程，可执行耗时操作
    public String unBunding(String json) throws JSONException {
        // 很多情况下返回的json是这种格式的:{"code": 0,"msg": "上传成功","data": [{"id": 1,"name": "huangxy",...}]}, 统一处理
        // 这里只适用于处理获取一个返回Json串，如果不需要统一处理建议定义一个泛型基类直接返回，参考BaseEntity<T>
        // 如果要同时获取多个data1、data2...，建议直接Entity返回
        // JSONArray array = new JSONArray(json);
        // JSONObject obj = new JSONObject(json);
        // return obj.getString("data");
        return json;
    }

    @Override
    public void onResponse(T response, int id) {
        if (response != null) onSuccess(response, id);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if (e instanceof JSONException  || e instanceof JsonIOException || e instanceof JsonSyntaxException){
            onParser(e, id);
            return;
        }
        // 网络请求错误，统一处理
        // ToastUtils.Show(R.drawable.ic_close, R.string.error_request, Toast.LENGTH_LONG);
        e.printStackTrace();
    }

    // 数据解析错误，统一处理
    public void onParser(Exception e, int id){
        // ToastUtils.Show(R.drawable.ic_close, R.string.error_parser, Toast.LENGTH_LONG);
        e.printStackTrace();
    }

    public abstract void onSuccess(T result, int id);
}
