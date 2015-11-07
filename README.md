# okhttp-utils
对okhttp的封装类，okhttp见：[https://github.com/square/okhttp](https://github.com/square/okhttp).



## 用法

* Android Studio

	使用前，对于Android Studio的用户，可以选择添加:

	```
	compile project(':okhttputils')
	```
	主项目中无需再引用okhttp的依赖，也不需要再额外导入Gson的lib.
	
* Eclipse
	
	下载[okhttputils.jar](okhttputils.jar)，添加到项目libs，同时需要下载[okhttp.jar](https://search.maven.org/remote_content?g=com.squareup.okhttp&a=okhttp&v=LATEST)和[gson-2.2.1.jar](gson-2.2.1.jar)


**注意**

由于整合了Gson，支持直接返回对象（例如`User`），对象集合(例如：`List<User>` )，所以记得使用时必须加入Gson的依赖，jar包[gson-2.2.1.jar](gson-2.2.1.jar).



##目前支持
* 一般的get请求
* 一般的post请求
* 基于Http的文件上传
* 文件下载
* 上传下载的进度回调
* 加载图片
* 支持请求回调，直接返回对象、对象集合
* 支持session的保持
* 支持自签名网站https的访问，提供方法设置下证书就行
* 支持取消某个请求


##用法示例

### GET请求

```java
//最基本
new OkHttpRequest.Builder()
	.url(url)
	.get(callback);
//扩展
new OkHttpRequest.Builder()
	.url(url)
	.params(params)
	.headers(headers)
	.tag(tag)
	.get(callback);
```

### POST请求

```java
//最基本
new OkHttpRequest.Builder()
	.url(url)
	.params(params)
	.post(callback);
//扩展
new OkHttpRequest.Builder()
	.url(url)
	.params(params)
	.headers(headers)
	.tag(tag)
	.post(callback);
```

### 上传文件

```java
//基本
new OkHttpRequest.Builder()
	.url(url)
	.files(files)
	.upload(callback);
//扩展
new OkHttpRequest.Builder()
	.url(url)
	.params(params)
	.headers(headers)
	.tag(tag)
	.files(files)
	.upload(callback);
```

### 下载文件

```java
//基本
new OkHttpRequest.Builder()
	.url(url)
	.destFileDir(destFileDir)
	.destFileName(destFileName)
	.download(callback);
//扩展
new OkHttpRequest.Builder()
	.url(url)
	.params(params)
	.headers(headers)
	.tag(tag)
	.destFileDir(destFileDir)
	.destFileName(destFileName)
	.download(callback);
```


### 显示图片

```java
//基本
 new OkHttpRequest.Builder()
	.url(url)
	.imageview(imageView)
	.displayImage(callback);
//扩展
new OkHttpRequest.Builder()
	.url(url)
	.params(params)
	.headers(headers)
	.tag(tag)
	.imageview(imageView)
	.errorResId(errorResId)
	.displayImage(callback);
```
会自动根据ImageView的大小进行压缩。

### CallBack支持泛型自动解析，返回对象或者集合

```java
//对象
new ResultCallback <User>()
{
    //...
    @Override
    public void onResponse(User user)
    {
        mTv.setText(user.username);
    }
}

//集合
new ResultCallback<List<User>>()
{
    //...
    @Override
    public void onResponse(List<User> users)
    {
        mTv.setText(users.get(0).username);
    }
}

```

注意如果返回值是String,需要填写泛型：`new ResultCallback<String>`

### 上传下载的进度显示

```java
new ResultCallback<List<User>>()
{
    //...
    @Override
    public void inProgress(float progress)
    {
       //use progress: 0 ~ 1
    }
}
```

复写callback的inProgress方法即可。


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
    public void inProgress(float progress)
    {
        
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
OkHttpRequest request 
	= new OkHttpRequest.Builder()
	    .url(url)
	    .tag(tag)
	    .get(callback);
//单个取消
request.cancel();               
```


```java

@Override
protected void onDestroy()
{
    super.onDestroy();
    //可以取消同一个tag的
    OkHttpClientManager.cancelTag(this);//取消以Activity.this作为tag的请求
}
```
比如，当前Activity页面所有的请求以Activity对象作为tag，可以在onDestory里面统一取消。


### 全局配置

可以在Application中，通过：

```java
OkHttpClient client = 
 OkHttpClientManager.getInstance().getOkHttpClient();
```
然后调用client的各种set方法。

例如：

```java
client.setConnectTimeout(100000, TimeUnit.MILLISECONDS);
```





