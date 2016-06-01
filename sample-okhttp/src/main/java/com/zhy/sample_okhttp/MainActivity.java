package com.zhy.sample_okhttp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String mBaseUrl = "http://192.168.56.1:8080/okHttpServer/";
    //    private String mGetUrl = "http://news-at.zhihu.com/api/4/news/latest";
    private String mGetUrl = "http://news-at.zhihu.com/api/4/themes";

    private static final String TAG = "MainActivity";

    private TextView mTv;
    private ImageView mImageView;
    private ProgressBar mProgressBar;


    public class MyStringCallback extends StringCallback {

        @Override
        public String parseNetworkResponse(Response response) throws IOException {
            Log.d("cache", "" + response.cacheResponse());
            Log.d("network", "" + response.networkResponse());

            return super.parseNetworkResponse(response);
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            setTitle("loading...");
        }

        @Override
        public void onAfter() {
            super.onAfter();
            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e) {
            e.printStackTrace();
            mTv.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            Log.e(TAG, "onResponse：complete");
            mTv.setText("onResponse:" + response);
        }

        @Override
        public void inProgress(float progress) {
            Log.e(TAG, "inProgress:" + progress);
            mProgressBar.setProgress((int) (100 * progress));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTv = (TextView) findViewById(R.id.id_textview);
        mImageView = (ImageView) findViewById(R.id.id_imageview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);
        mProgressBar.setMax(100);
    }

    public void getHtml(View view) {
        OkHttpUtils
                .get()
                .url(mGetUrl)
                .build()
                .execute(new MyStringCallback());

    }

    public void getUser(View view) {
//        String url = mBaseUrl + "user!getUser";
//        OkHttpUtils
//                .getUser()
//                .url(url)
//                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .content(new Gson().toJson(new User("zhy", "123")))
//                .build()
//                .execute(new MyStringCallback());
        //        String url = "http://www.baidu.com";
        String url = "http://123.123.123.123:8080/tlyht/user/info";
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Accept", "application/json")
                .build()
//                .cacheControl(CacheControl.FORCE_NETWORK)
                .execute(new MyStringCallback());

    }

    public void delete(View view) {
//        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
//        if (!file.exists()) {
//            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        String url = mBaseUrl + "user!delete";
//        OkHttpUtils
//                .delete()
//                .url(url)
//                .file(file)
//                .build()
//                .execute(new MyStringCallback());
        try {
            OkHttpUtils.getInstance().getOkHttpClient().cache().delete();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void login(View view) {
        String url = "http://123.123.123.123:8080/tlyht/user/login";
        OkHttpUtils//
                .post()//
                .url(url)//
                .addHeader("Accept", "application/json")
                .addParams("phone", "13026100183")//
                .addParams("password", "23456")//
                .build()//
                .execute(new MyStringCallback());
    }


    public void getMac(View view) {
        String url = "http://123.123.123.123:8080/tlyht/user/findDeviceValue";
        OkHttpUtils
                .post()//
                .url(url)//
                .addHeader("Accept", "application/json")
//                .addHeader("Cache-Control", "max-age=15")
                .addParams("mac", "16:05:00:00:68:D4")//
                .build()//
                .execute(new MyStringCallback());
    }

    public void postOutThis(View view) {
//        String url = "http://123.123.123.123:8080/tlyht/user/findDeviceValue";
        String url = "http://123.123.123.123:8080/tlyht/user/info";
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("mac", "16:05:00:00:68:D4")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("contentType", "application/x-www-form-urlencoded")
//                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("cache", "" + response.cacheResponse());
                    Log.d("network", "" + response.networkResponse());
                    Log.d("response", "" + response.body().string());
                }
            }
        });


    }


    public void getHttpsHtml(View view) {
        String url = "https://kyfw.12306.cn/otn/";

        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .execute(new MyStringCallback());

    }

    public void getImage(View view) {
        mTv.setText("");
        String url = "http://images.csdn.net/20150817/1.jpg";
        OkHttpUtils
                .get()//
                .url(url)//
                .tag(this)//
                .build()//
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        mTv.setText("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Log.e("TAG", "onResponse：complete");
                        mImageView.setImageBitmap(bitmap);
                    }
                });
    }


    public void uploadFile(View view) {

        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "张鸿洋");
        params.put("password", "123");

        Map<String, String> headers = new HashMap<>();
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");

        String url = mBaseUrl + "user!uploadFile";

        OkHttpUtils.post()//
                .addFile("mFile", "messenger_01.png", file)//
                .url(url)//
                .params(params)//
                .headers(headers)//
                .build()//
                .execute(new MyStringCallback());
    }


    public void multiFileUpload(View view) {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        File file2 = new File(Environment.getExternalStorageDirectory(), "test1#.txt");
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "张鸿洋");
        params.put("password", "123");

        String url = mBaseUrl + "user!uploadFile";
        OkHttpUtils.post()//
                .addFile("mFile", "messenger_01.png", file)//
                .addFile("mFile", "test1.txt", file2)//
                .url(url)
                .params(params)//
                .build()//
                .execute(new MyStringCallback());
    }


    public void downloadFile(View view) {
        String url = "https://github.com/hongyangAndroid/okhttp-utils/blob/master/okhttputils-2_4_1.jar?raw=true";
        OkHttpUtils//
                .get()//
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1.jar")//
                {

                    @Override
                    public void onBefore(Request request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void inProgress(float progress, long total) {
                        mProgressBar.setProgress((int) (100 * progress));
                        Log.e(TAG, "inProgress :" + (int) (100 * progress));
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e(TAG, "onError :" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File file) {
                        Log.e(TAG, "onResponse :" + file.getAbsolutePath());
                    }
                });
    }


    public void otherRequestDemo(View view) {
        //also can use delete ,head , patch
        /*
        OkHttpUtils
                .put()//
                .url("http://11111.com")
                .requestBody
                        ("may be something")//
                .build()//
                .execute(new MyStringCallback());



        OkHttpUtils
                .head()//
                .url(url)
                .addParams("name", "zhy")
                .build()
                .execute();

       */


    }

    public void clearSession(View view) {
        CookieJar cookieJar = OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
        if (cookieJar instanceof CookieJarImpl) {
            ((CookieJarImpl) cookieJar).getCookieStore().removeAll();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
