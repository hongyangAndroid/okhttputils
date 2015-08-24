# okhttp-utils
okhttp的辅助类


由于整合了Gson，支持直接返回对象（例如`User`），对象集合(例如：`List<User>` )，所以记得使用时必须加入Gson的依赖，jar包[gson-2.2.1.jar](gson-2.2.1.jar).

#目前支持
* 一般的get请求
* 一般的post请求
* 基于Http的文件上传
* 文件下载
* 加载图片
* 支持请求回调，直接返回对象、对象集合

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
        OkHttpClientManager.postAsyn("http://192.168.1.103:8080/okHttpServer/fileUpload",//
                callback,//
                file,// 文件
                "mFile",// 文件域的name
                new OkHttpClientManager.Param[]{
                new OkHttpClientManager.Param("username", "zhy")}//一般的键值对，可为null
        );
```

### 显示图片

```java
 OkHttpClientManager.displayImage(
 				mImageView, 
 				"http://images.csdn.net/20150817/1.jpg");

```
会自动根据ImageView的大小进行压缩。

### 大文件下载

```java
 OkHttpClientManager.downloadAsyn(
 		//文件路径
 		"http://192.168.1.103:8080/okHttpServer/files/messenger_01.png", 		//文件存储路径
 		Environment.getExternalStorageDirectory().getAbsolutePath(), 
 		//回调
 		new OkHttpClientManager.ResultCallback<String>()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
            }

            @Override
            public void onResponse(String response)
            {
					//如果成功，response为下载完成后文件的完整路径
            }
        });
 ```