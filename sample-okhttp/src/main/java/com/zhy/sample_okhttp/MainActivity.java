package com.zhy.sample_okhttp;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.callback.ResultCallback;
import com.zhy.http.okhttp.request.OkHttpRequest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    private TextView mTv;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public abstract class MyResultCallback<T> extends ResultCallback<T>
    {

        @Override
        public void onBefore(Request request)
        {
            super.onBefore(request);
            setTitle("loading...");
        }

        @Override
        public void onAfter()
        {
            super.onAfter();
            setTitle("Sample-okHttp");
        }
    }

    private ResultCallback<String> stringResultCallback = new MyResultCallback<String>()//
    {
        @Override
        public void onError(Request request, Exception e)
        {
            Log.e("TAG", "onError , e = " + e.getMessage());
        }

        @Override
        public void onResponse(String response)
        {
            Log.e("TAG", "onResponse , response = " + response);
            mTv.setText("operate success");
        }

        @Override
        public void inProgress(float progress)
        {
            mProgressBar.setProgress((int) (100 * progress));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv = (TextView) findViewById(R.id.id_textview);
        mImageView = (ImageView) findViewById(R.id.id_imageview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);
        mProgressBar.setMax(100);
    }

    public void getUser(View view)
    {

        final String url = "https://raw.githubusercontent.com/hongyangAndroid/okhttp-utils/master/user.gson";
        new OkHttpRequest.Builder()
                .url(url)
                .get(new MyResultCallback<User>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        Log.e("TAG", "onError , e = " + e.getMessage());
                    }

                    @Override
                    public void onResponse(User response)
                    {
                        Log.e("TAG", "onResponse , user = " + response);
                        mTv.setText(response.username);
                    }
                });

        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    User u = new OkHttpRequest.Builder().url(url).get(User.class);
                    Log.e("TAG", "syn u = " + u);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }.start();


    }


    public void getUsers(View view)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "zhy");
        String url = "https://raw.githubusercontent.com/hongyangAndroid/okhttp-utils/master/users.gson";
        new OkHttpRequest.Builder().url(url).params(params).post(new MyResultCallback<List<User>>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e("TAG", "onError , e = " + e.getMessage());
            }

            @Override
            public void onResponse(List<User> users)
            {
                Log.e("TAG", "onResponse , users = " + users);
                mTv.setText(users.get(0).toString());
            }
        });


    }

    public void getSimpleString(View view)
    {
        String url = "https://raw.githubusercontent.com/hongyangAndroid/okhttp-utils/master/user.gson";

        new OkHttpRequest.Builder().url(url)
                .get(new MyResultCallback<String>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        Log.e("TAG", "onError , e = " + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        mTv.setText(response);
                    }
                });


    }

    public void getHtml(View view)
    {
        //https://192.168.56.1:8443/
        //https://kyfw.12306.cn/otn/
        //https://192.168.187.1:8443/
        String url = "http://www.csdn.net/";
        new OkHttpRequest.Builder().url(url).get(new MyResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e("TAG", "onError" + e.getMessage());
            }

            @Override
            public void onResponse(String response)
            {
                mTv.setText(response);
            }
        });
    }

    public void getHttpsHtml(View view)
    {
        String url = "https://kyfw.12306.cn/otn/";
        new OkHttpRequest.Builder().url(url).get(new MyResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e("TAG", "onError" + e.getMessage());
            }

            @Override
            public void onResponse(String response)
            {
                mTv.setText(response);
            }
        });
    }

    public void getImage(View view)
    {
        String url = "http://images.csdn.net/20150817/1.jpg";
        mTv.setText("");
        new OkHttpRequest.Builder().url(url).imageView(mImageView).displayImage(null);
    }


    public void uploadFile(View view)
    {

        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        if (!file.exists())
        {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "张鸿洋");
        params.put("password", "123");

        Map<String, String> headers = new HashMap<>();
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");

        String url = "http://192.168.1.103:8080/okHttpServer/fileUpload";
        new OkHttpRequest.Builder()//
                .url(url)//
                .params(params)
                .headers(headers)
                .files(new Pair<String, File>("mFile", file))//
                .upload(stringResultCallback);
    }


    public void multiFileUpload(View view)
    {
        File file = new File(Environment.getExternalStorageDirectory(), "messenger_01.png");
        File file2 = new File(Environment.getExternalStorageDirectory(), "test1.txt");
        if (!file.exists())
        {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", "张鸿洋");
        params.put("password", "123");

        String url = "http://192.168.1.103:8080/okHttpServer/mulFileUpload";
        new OkHttpRequest.Builder()//
                .url(url)//
                .params(params)
                .files(new Pair<String, File>("mFile", file), new Pair<String, File>("mFile", file2))//
                .upload(stringResultCallback);


    }


    public void downloadFile(View view)
    {
        String url = "https://github.com/hongyangAndroid/okhttp-utils/blob/master/gson-2.2.1.jar?raw=true";
        new OkHttpRequest.Builder()
                .url(url)
                .destFileDir(Environment.getExternalStorageDirectory().getAbsolutePath())
                .destFileName("gson-2.2.1.jar")
                .download(stringResultCallback);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        OkHttpClientManager.cancelTag(this);
    }
}
