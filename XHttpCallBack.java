package com.huangxy.XhttpUtils;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Git@Smark on 2016/7/17.
 * 简化接口，支持返回String、Entity、List<Entity>
 */
public abstract class XHttpCallBack<T> extends Callback<T> {

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        if (getClass().getGenericSuperclass() == XHttpCallBack.class){
            return (T)result;// 默认返回String
        }
        if(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] == String.class){
            return (T)result;// 返回String类型
        }
        // 这句重点，同时支持T、List<T>等等
        Type type = $Gson$Types.canonicalize(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        try {
            return new Gson().fromJson(result, type);
        }catch (Exception e){
            onParser(e, id);
        }
        return null;
    }

    @Override
    public void onResponse(T response, int id) {
        onSuccess(response, id);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        System.out.println("网络请求错误，统一处理"+e);
    }

    public void onParser(Exception e, int id){
        System.out.println("数据解析错误，统一处理"+e);
    }

    public abstract void onSuccess(T result, int id);
}
