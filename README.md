# okhttp-utils
okhttp的辅助类


由于整合了Gson，支持直接返回对象（例如`User`），对象集合(例如：`List<User>` )，所以记得使用时必须加入Gson的依赖，jar包[gson-2.2.1.jar](gson-2.2.1.jar).

sample项目的代码也上传了，里面包含依赖神马的，不过里面的请求url可能部分是无法使用的，因为我是本机做的测试，大家可以下载参考。ps:以module的形式导入。

#目前支持
* 一般的get请求
* 一般的post请求
* 基于Http的文件上传
* 文件下载
* 加载图片
* 支持请求回调，直接返回对象、对象集合
* 支持session的保持
* 支持自签名网站https的访问，提供方法设置下证书就行

例如：服务器返回：`{"username":"zhy","password":"123"}`

客户端可按照如下发起请求，直接获得User对象

```java
 OkHttpClientManager.getAsyn("http://192.168.56.1:8080/okHttpServer/user!getUser",
new OkHttpClientManager.ResultCallback<User>()
{
    @Override
    public void onError(Request request, Exception e)
    {
        e.printStackTrace();
    }

    @Override
    public void onResponse(User user)
    {
        mTv.setText(u.toString());//UI线程
    }
});
```
同样支持List<User>这种集合的方式。


# 用法

对于Android Studio的用户，可以选择添加:

```xml
compile 'com.squareup.okhttp:okhttp:2.4.0'

```
或者Eclipse的用户，可以下载最新的jar [okhttp he latest JAR](https://search.maven.org/remote_content?g=com.squareup.okhttp&a=okhttp&v=LATEST) ，添加依赖就可以用了。

注意:okhttp内部依赖okio，别忘了同时导入okio：

`compile 'com.squareup.okio:okio:1.5.0'`

最新的jar地址：[okio the latest JAR](https://search.maven.org/remote_content?g=com.squareup.okio&a=okio&v=LATEST)


### GET请求

```java
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
```

### 直接返回对象

```java
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

```
注意会根据传入的泛型，比如User，则会将服务器返回的Json字符串转化为user对象，如果在转化过程中发生错误，onError会被回调。记得加入Gson的依赖。


### 直接返回对象集合

```java
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
```
注意会根据传入的泛型，比如List<User>，则会将服务器返回的Json字符串转化为List<User>对象，如果在转化过程中发生错误，onError会被回调。记得加入Gson的依赖。

### POST请求

```java
 OkHttpClientManager.postAsyn(url,callback,params);
```

### 基于HTTP POST的大文件上传（可包一般键值对）

```java
File file = new File(Environment.getExternalStorageDirectory(), "test1.txt");

if (!file.exists())
{
 
  return;
}

OkHttpClientManager.getUploadDelegate().postAsyn("http://192.168.1.103:8080/okHttpServer/fileUpload",//
      "mFile",//
      file,//
      new OkHttpClientManager.Param[]{
              new OkHttpClientManager.Param("username", "zhy"),
              new OkHttpClientManager.Param("password", "123")},//
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
      }
);
```

### 显示图片

```java
  OkHttpClientManager.getDisplayImageDelegate()
  	.displayImage(mImageView, 
  		"http://images.csdn.net/20150817/1.jpg");
 

```
会自动根据ImageView的大小进行压缩。

### 大文件下载

```java
 OkHttpClientManager.getDownloadDelegate().downloadAsyn(
 "url",
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
         Toast.makeText(MainActivity.this, response + "下载成功", Toast.LENGTH_SHORT).show();
     }
 });
```

### 自签名网站https的访问

非常简单，拿到xxx.cert的证书。

然后调用

```xml

OkHttpClientManager.getInstance()
       .setCertificates(inputstream);
```

建议使用方式，例如我的证书放在assets目录：

```java

/**
 * Created by zhy on 15/8/25.
 */
public class MyApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        try
        {
            OkHttpClientManager.getInstance()
                    .setCertificates(getAssets().open("aaa.cer"),
                            getAssets().open("server.cer"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
```
即可。别忘了注册Application。

