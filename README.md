# okhttp-utils
okhttp的辅助类

[toc]

## 用法

* Android Studio

	使用前，对于Android Studio的用户，可以选择添加:

	```
	compile 'com.squareup.okhttp:okhttp:2.4.0'
	compile 'com.squareup.okio:okio:1.5.0'

	```

* Eclispe

	Eclipse的用户，可以下载jar [okhttp  JAR](https://search.maven.org/remote_content?g=com.squareup.okhttp&a=okhttp&v=LATEST)和 [okio JAR](https://search.maven.org/remote_content?g=com.squareup.okio&a=okio&v=LATEST)添加依赖就可以用了。

**注意**

由于整合了Gson，支持直接返回对象（例如`User`），对象集合(例如：`List<User>` )，所以记得使用时必须加入Gson的依赖，jar包[gson-2.2.1.jar](gson-2.2.1.jar).

sample项目的代码也上传了，大家可以下载参考里面的依赖，以及示例用法。ps:以module的形式导入。

最后将[OkHttpClientManager](OkHttpClientManager.java)拷贝到项目即可。

##目前支持
* 一般的get请求
* 一般的post请求
* 基于Http的文件上传
* 文件下载
* 加载图片
* 支持请求回调，直接返回对象、对象集合
* 支持session的保持
* 支持自签名网站https的访问，提供方法设置下证书就行
* 支持取消某个请求


##用法示例

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
		.getHttpsDelegate()
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
            			.getHttpsDelegate()
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


### 高级用法

ResultCallback包含两个回调，`onBefore`和`onAfter`。两个方法都在UI线程回调，一个在请求开始前，一个是请求结束。所以你可以在`onBefore `弹出等待框等操作，`onAfter`隐藏等待框等。

```java
OkHttpClientManager.getAsyn("http://192.168.56.1:8080/okHttpServer/user!getUser",
new OkHttpClientManager.ResultCallback<User>()
{
	@Override
    public void onBefore(Request request)
    {
        showWaitingDialog();
    }
    
    @Override
    public void onAfter()
    {
        dismissWaitingDialog();
    }
	
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

如果你的项目所有的框是一致的，或者可以分类，你可以按照如下方式编写几个模板：

```java
public abstract class MyResultCallback<T> extends ResultCallback<T>
{

   @Override
   public void onBefore()
   {
       super.onBefore();
       //显示等待框等
       setTitle("loading...");
   }

   @Override
   public void onAfter()
   {
       super.onAfter();
       //隐藏等待框等
       setTitle("Sample-okHttp");
   }
}

```

### 如何取消某个请求

目前对于支持的方法都添加了最后一个参数`Object tag`，取消则通过` OkHttpClientManager.cancelTag(tag)`执行。

例如：在Activity中，当Activity销毁取消某个请求：

```java
OkHttpClientManager.getAsyn("http://www.csdn.net/", new MyResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e("TAG", "onError" + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(String u)
            {
                Log.e("TAG", "onResponse" + MainActivity.this);
                mTv.setText(u);
            }
        }, this);//注意这里将Activity.this作为tag
        
```
Activity的onDestory中

```java
@Override
    protected void onDestroy()
    {
        super.onDestroy();
        OkHttpClientManager.cancelTag(this);//取消以Activity.this作为tag的请求
    }
```


### 说明

目前比较常见的API可以直接通过OkHttpClientManager.methodName访问，当然有很多不常用的方法，会被封装在对应的模块内部，大体分为以下几个模块：

* HttpsDelegate
* DownloadDelegate
* DisplayImageDelegate
* GetDelegate
* UploadDelegate
* PostDelegate

如果你有比较特殊的需求，不放通过OkHttpClientManager.getXXXDelegate().methodName去访问。

比如加载图片：

```java
OkHttpClientManager.getDisplayImageDelegate().displayImage();
```

比如get请求，直接将文件作为请求体：

```java
OkHttpClientManager.getPostDelegate().post(url,file,callback);
```

### 没有提供的方法？

对于get、post方式的方法，如果工具类中没有提供，那么可以通过如下方式：

#### Get

```java
//同步
OkHttpClientManager.getHttpDelegate().get(request);
//异步
OkHttpClientManager.getHttpDelegate().getAsyn(request, callback);

```

#### Post

```java
//同步
OkHttpClientManager.getPostDelegate().post(request);
//异步
OkHttpClientManager.getPostDelegate().postAsyn(request, callback);

```

自己去构造Request.

如果还不能满足你的需求，那么只好整个过程都自己去书写了，但是你肯定不希望项目有中出现两个`OkHttpClient`对象，那么对于`OkHttpClient`对象你可以通过

```java
OkHttpClient client = OkHttpClientManager.getClient();
```
进行获取。



