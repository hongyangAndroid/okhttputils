# okhttp-utils
对okhttp的封装类，okhttp见：[https://github.com/square/okhttp](https://github.com/square/okhttp).



## 用法

* Android Studio

	使用前，对于Android Studio的用户，可以选择添加:

	```
	compile project(':okhttputils')
	```
	
	或者
	
	```
	compile 'com.zhy:okhttputils:2.0.2'
	```
	
* Eclipse
	
	自行copy源码。
	

**注意**

目前最新版本取消了依赖Gson，提供了自定Callback，可以按照下面的方式，自行解析返回结果：

```java
public abstract class UserCallback extends Callback<User>
{
	//非UI线程，支持任何耗时操作
    @Override
    public User parseNetworkResponse(Response response) throws IOException
    {
        String string = response.body().string();
        User user = new Gson().fromJson(string, User.class);
        return user;
    }
}
```



##目前支持
* 一般的get请求
* 一般的post请求
* 基于Http Post的文件上传（类似表单）
* 文件下载/加载图片
* 上传下载的进度回调
* 支持session的保持
* 支持自签名网站https的访问，提供方法设置下证书就行
* 支持取消某个请求
* 支持自定义Callback

##用法示例

### GET请求

```java
String url = "http://www.csdn.net/";
OkHttpUtils
    .get()
    .url(url)
    .addParams("username", "hyman")
    .addParams("password", "123")
    .build()
    .execute(new StringCallback()
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
```

### POST请求

```java
 OkHttpUtils
    .post()
    .url(url)
    .addParams("username", "hyman")
    .addParams("password", "123")
    .build()
    .execute(callback);

```

### Post String

```java
  OkHttpUtils
    .postString()
    .url(url)
    .content(new Gson().toJson(new User("zhy", "123")))
    .build()
    .execute(new MyStringCallback());
```

提交一个Gson字符串到服务器端。

### Post File

```java
 OkHttpUtils
	.postFile()
	.url(url)
	.file(file)
	.build()
	.execute(new MyStringCallback());
```
将文件作为请求体，发送到服务器。


### Post表单形式上传文件

```java
OkHttpUtils.post()//
    .addFile("mFile", "messenger_01.png", file)//
    .addFile("mFile", "test1.txt", file2)//
    .url(url)
    .params(params)//
    .headers(headers)//
    .build()//
    .execute(new MyStringCallback());
```

支持单个多个文件，`addFile`的第一个参数为文件的key，即类别表单中`<input type="file" name="mFile"/>`的name属性。

### 自定义CallBack

目前内部包含`StringCallBack`,`FileCallBack`,`BitmapCallback`，可以根据自己的需求去自定义Callback，例如希望回调User对象：

```java
public abstract class UserCallback extends Callback<User>
{
    @Override
    public User parseNetworkResponse(Response response) throws IOException
    {
        String string = response.body().string();
        User user = new Gson().fromJson(string, User.class);
        return user;
    }
}

 OkHttpUtils
    .get()//
    .url(url)//
    .addParams("username", "hyman")//
    .addParams("password", "123")//
    .build()//
    .execute(new UserCallback()
    {
        @Override
        public void onError(Request request, Exception e)
        {
            mTv.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(User response)
        {
            mTv.setText("onResponse:" + response.username);
        }
    });

```

通过`parseNetworkResponse `回调的response进行解析，该方法运行在子线程，所以可以进行任何耗时操作，详细参见sample。


### 下载文件

```java
 OkHttpUtils//
	.get()//
	.url(url)//
	.build()//
	.execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "gson-2.2.1.jar")//
	{
	    @Override
	    public void inProgress(float progress)
	    {
	        mProgressBar.setProgress((int) (100 * progress));
	    }
	
	    @Override
	    public void onError(Request request, Exception e)
	    {
	        Log.e(TAG, "onError :" + e.getMessage());
	    }
	
	    @Override
	    public void onResponse(File file)
	    {
	        Log.e(TAG, "onResponse :" + file.getAbsolutePath());
	    }
	});
```

注意下载文件可以使用`FileCallback`，需要传入文件需要保存的文件夹以及文件名。


### 显示图片

```java
 OkHttpUtils
    .get()//
    .url(url)//
    .build()//
    .execute(new BitmapCallback()
    {
        @Override
        public void onError(Request request, Exception e)
        {
            mTv.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(Bitmap bitmap)
        {
            mImageView.setImageBitmap(bitmap);
        }
    });
```

显示图片，回调传入`BitmapCallback`即可。


### 上传下载的进度显示

```java
new Callback<T>()
{
    //...
    @Override
    public void inProgress(float progress)
    {
       //use progress: 0 ~ 1
    }
}
```

callback回调中有`inProgress `方法，直接复写即可。

### 同步的请求

```
 Response response = OkHttpUtils
    .get()//
    .url(url)//
    .tag(this)//
    .build()//
    .execute();
```

execute方法不传入callback即为同步的请求，返回Response。

## 配置

### 全局配置

可以在Application中，通过：

```java
OkHttpClient client = 
OkHttpUtils.getInstance().getOkHttpClient();
```
然后调用client的各种set方法。

例如：

```java
client.setConnectTimeout(100000, TimeUnit.MILLISECONDS);
```

### 为单个请求设置超时

比如涉及到文件的需要设置读写等待时间多一点。

```java
 OkHttpUtils
    .get()//
    .url(url)//
    .tag(this)//
    .build()//
    .connTimeOut(20000)
    .readTimeOut(20000)
    .writeTimeOut(20000)
    .execute()
```
调用build()之后，可以随即设置各种timeOut.

### 自签名网站https的访问

非常简单，拿到xxx.cert的证书。

然后调用

```xml
OkHttpUtils.getInstance()
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


### 取消单个请求

```java
 RequestCall call = OkHttpUtils.get().url(url).build();
 call.cancel();
 
```

### 根据tag取消请求

目前对于支持的方法都添加了最后一个参数`Object tag`，取消则通过` OkHttpUtils.cancelTag(tag)`执行。

例如：在Activity中，当Activity销毁取消请求：

```
OkHttpUtils
    .get()//
    .url(url)//
    .tag(this)//
    .build()//

@Override
protected void onDestroy()
{
    super.onDestroy();
    //可以取消同一个tag的
    OkHttpUtils.cancelTag(this);//取消以Activity.this作为tag的请求
}
```
比如，当前Activity页面所有的请求以Activity对象作为tag，可以在onDestory里面统一取消。








