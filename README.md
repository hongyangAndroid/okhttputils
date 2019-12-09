# okhttp-utils

>由于个人原因，现已停止维护。

对okhttp的封装类，okhttp见：[https://github.com/square/okhttp](https://github.com/square/okhttp).

目前对应okhttp版本`3.3.1`.

## 用法

* Android Studio
	
	```
	compile 'com.zhy:okhttputils:2.6.2'
	```
	
* Eclipse
	
	下载最新jar:[okhttputils-2\_6\_2.jar](okhttputils-2_6_2.jar?raw=true)

	注：需要同时导入okhttp和okio的jar，下载见：[https://github.com/square/okhttp](https://github.com/square/okhttp).
	

## 目前对以下需求进行了封装
* 一般的get请求
* 一般的post请求
* 基于Http Post的文件上传（类似表单）
* 文件下载/加载图片
* 上传下载的进度回调
* 支持取消某个请求
* 支持自定义Callback
* 支持HEAD、DELETE、PATCH、PUT
* 支持session的保持
* 支持自签名网站https的访问，提供方法设置下证书就行

## 配置OkhttpClient

默认情况下，将直接使用okhttp默认的配置生成OkhttpClient，如果你有任何配置，记得在Application中调用`initClient`方法进行设置。

```java
public class MyApplication extends Application
{	
	@Override
    public void onCreate()
    {
        super.onCreate();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                  .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                  .readTimeout(10000L, TimeUnit.MILLISECONDS)
                  //其他配置
                 .build();
                 
        OkHttpUtils.initClient(okHttpClient);

    }
}
```
别忘了在AndroidManifest中设置。

## 对于Cookie(包含Session)

对于cookie一样，直接通过cookiejar方法配置，参考上面的配置过程。

```
CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
OkHttpClient okHttpClient = new OkHttpClient.Builder()
          .cookieJar(cookieJar)
          //其他配置
         .build();
                 
OkHttpUtils.initClient(okHttpClient);
```
目前项目中包含：

* PersistentCookieStore //持久化cookie
* SerializableHttpCookie //持久化cookie
* MemoryCookieStore //cookie信息存在内存中

如果遇到问题，欢迎反馈，当然也可以自己实现CookieJar接口，编写cookie管理相关代码。

此外，对于持久化cookie还可以使用[https://github.com/franmontiel/PersistentCookieJar](https://github.com/franmontiel/PersistentCookieJar).

相当于框架中只是提供了几个实现类，你可以自行定制或者选择使用。

## 对于Log

初始化OkhttpClient时，通过设置拦截器实现，框架中提供了一个`LoggerInterceptor `，当然你可以自行实现一个Interceptor 。

```
 OkHttpClient okHttpClient = new OkHttpClient.Builder()
       .addInterceptor(new LoggerInterceptor("TAG"))
        //其他配置
        .build();
OkHttpUtils.initClient(okHttpClient);
```


## 对于Https

依然是通过配置即可，框架中提供了一个类`HttpsUtils`

* 设置可访问所有的https网站

```
HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
         //其他配置
         .build();
OkHttpUtils.initClient(okHttpClient);
```

* 设置具体的证书

```
HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(证书的inputstream, null, null);
OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager))
         //其他配置
         .build();
OkHttpUtils.initClient(okHttpClient);
```

* 双向认证

```
HttpsUtils.getSslSocketFactory(
	证书的inputstream, 
	本地证书的inputstream, 
	本地证书的密码)
```

同样的，框架中只是提供了几个实现类，你可以自行实现`SSLSocketFactory`，传入sslSocketFactory即可。

##其他用法示例

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

### Post JSON

```java
  OkHttpUtils
    .postString()
    .url(url)
    .content(new Gson().toJson(new User("zhy", "123")))
     .mediaType(MediaType.parse("application/json; charset=utf-8"))
    .build()
    .execute(new MyStringCallback());
```

提交一个Gson字符串到服务器端，注意：传递JSON的时候，不要通过addHeader去设置contentType，而使用`.mediaType(MediaType.parse("application/json; charset=utf-8"))`.。

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

### HEAD、DELETE、PUT、PATCH

```java

OkHttpUtils
     .put()//also can use delete() ,head() , patch()
     .requestBody(RequestBody.create(null, "may be something"))//
     .build()//
     .execute(new MyStringCallback());
```

如果需要requestBody，例如：PUT、PATCH，自行构造进行传入。



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

## 混淆

```
#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}


#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}


#okio
-dontwarn okio.**
-keep class okio.**{*;}


```


Apache License
                           Version 2.0, January 2004
                        https://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

   1. Definitions.

      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.

      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.

      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.

      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.

      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.

      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.

      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).

      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.

      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."

      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.

   2. Grant of Copyright License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.

   3. Grant of Patent License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.

   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:

      (a) You must give any other recipients of the Work or
          Derivative Works a copy of this License; and

      (b) You must cause any modified files to carry prominent notices
          stating that You changed the files; and

      (c) You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and

      (d) If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.

      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.

   5. Submission of Contributions. Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.

   6. Trademarks. This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.

   7. Disclaimer of Warranty. Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.

   8. Limitation of Liability. In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.

   9. Accepting Warranty or Additional Liability. While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.

   END OF TERMS AND CONDITIONS

   APPENDIX: How to apply the Apache License to your work.

      To apply the Apache License to your work, attach the following
      boilerplate notice, with the fields enclosed by brackets "[]"
      replaced with your own identifying information. (Don't include
      the brackets!)  The text should be enclosed in the appropriate
      comment syntax for the file format. We also recommend that a
      file or class name and description of purpose be included on the
      same "printed page" as the copyright notice for easier
      identification within third-party archives.

   Copyright 2019 Rolando Gopez Lacuata.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

