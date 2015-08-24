package com.imooc.sample_okhttp;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zhy.utils.http.okhttp.OkHttpClientManager;
import com.zhy.utils.http.okhttp.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private TextView mTv;
    private ImageView mImageView;
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mOkHttpClient = new OkHttpClient();

        mTv = (TextView) findViewById(R.id.id_textview);
        mImageView = (ImageView) findViewById(R.id.id_imageview);

    }

    public void getUser(View view)
    {
        OkHttpClientManager.getAsyn("http://192.168.56.1:8080/okHttpServer/user!getUser",
                new OkHttpClientManager.ResultCallback<User>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(User u)
                    {
                        mTv.setText(u.toString());
                    }
                });
    }


    public void getUsers(View view)
    {
        OkHttpClientManager.getAsyn("http://192.168.56.1:8080/okHttpServer/user!getUsers",
                new OkHttpClientManager.ResultCallback<List<User>>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(List<User> us)
                    {
                        Log.e("TAG", us.size() + "");
                        mTv.setText(us.get(1).toString());
                    }
                });


    }

    public void getSimpleString(View view)
    {
        OkHttpClientManager.getAsyn("http://192.168.56.1:8080/okHttpServer/user!getSimpleString", new OkHttpClientManager.ResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String u)
            {
                mTv.setText(u);
            }
        });


    }

    public void getHtml(View view)
    {
        OkHttpClientManager.getAsyn("https://github.com/hongyangAndroid", new OkHttpClientManager.ResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String u)
            {
                mTv.setText(u);
            }
        });


    }

    public void getImage(View view)
    {
        OkHttpClientManager.displayImage(mImageView, "http://images.csdn.net/20150817/1.jpg");
    }

    public void uploadFile(View view) throws IOException
    {
        File file = new File(Environment.getExternalStorageDirectory(), "test1.txt");

        /*OkHttpClientManager.postAsyn("http://192.168.1.103:8080/okHttpServer/fileUpload", null, new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("username", "zhy"),
                new OkHttpClientManager.Param("password", "123")});
                */
        OkHttpClientManager.postAsyn("http://192.168.1.103:8080/okHttpServer/fileUpload",//
                new OkHttpClientManager.ResultCallback<String>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String filePath)
                    {
                        Log.e("TAG", filePath);
                    }
                },//
                file,//
                "mFile",//
                new OkHttpClientManager.Param[]{
                        new OkHttpClientManager.Param("username", "zhy"),
                        new OkHttpClientManager.Param("password", "123")}
        );
    }


    public void downloadFile(View view)
    {
        OkHttpClientManager.downloadAsyn("http://192.168.1.103:8080/okHttpServer/files/messenger_01.png",
                Environment.getExternalStorageDirectory().getAbsolutePath(),
                new OkHttpClientManager.ResultCallback<String>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                    }

                    @Override
                    public void onResponse(String response)
                    {
                    }
                });
    }


}
