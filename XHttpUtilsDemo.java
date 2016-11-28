package com.huangxy.XhttpUtils;

import com.administrator.ticat.Entity.User;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

/**
 * Created by Git@Smark on 2016/7/17.
 */
public class XHttpUtilsDemo {

    public XHttpUtilsDemo(){

        String url = "https://github.com/GitSmark";

        OkHttpUtils
                .get()
                .url(url)
                .addParams("param1", "value")
                .addParams("param2", "value")
                .build()
                .execute(new XHttpCallBack() {

                    @Override
                    public void onSuccess(Object result, int id) {
                        //Success
                    }
                });

        OkHttpUtils
                .post()
                .url(url)
                .addParams("param1", "value")
                .addParams("param2", "value")
                .build()
                .execute(new XHttpCallBack<User>() {

                    @Override
                    public void onSuccess(User result, int id) {
                        //Success
                    }
                });

        OkHttpUtils
                .post()
                .url(url)
                .addParams("param1", "value")
                .addParams("param2", "value")
                .build()
                .execute(new XHttpCallBack<List<User>>() {

                    @Override
                    public void onSuccess(List<User> result, int id) {
                        //Success
                    }

                    //@Override
                    //public void onParser(Exception e, int id) {
                    //   super.onParser(e, id);
                    //}
                });
    }
}
