# okhttp-utils
okhttp的辅助类


# 用法

### GET请求

```java
OkHttpClientManager.getAsyn("https://github.com/hongyangAndroid", 
new OkHttpClientManager.StringCallback()
        {
            @Override
            public void onFailure(Request request, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String bytes)
            {
                mTv.setText(bytes);
            }
        });
```

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
 		new OkHttpClientManager.StringCallback()
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