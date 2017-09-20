package com.jrmf360.neteaselib.base.http;

import android.content.Context;

import com.jrmf360.neteaselib.JrmfClient;
import com.jrmf360.neteaselib.base.utils.CertificateUtil;
import com.jrmf360.neteaselib.base.utils.LogUtil;
import com.jrmf360.neteaselib.base.utils.ThreadUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @创建人 honglin
 * @创建时间 17/1/1 下午7:41
 * @类描述 利用okhttp请求网络
 */
public class OkHttpWork {

    private static OkHttpClient okHttpClient;

    private static OkHttpWork instance = null;

    private OkHttpWork() {
        okHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .sslSocketFactory(CertificateUtil.getSocketFactory(JrmfClient.getAppContext())).
                hostnameVerifier(DO_NOT_VERIFY)
                .build();
    }

    public static OkHttpWork getInstance() {
        synchronized (OkHttpWork.class) {
            if (instance == null) {
                instance = new OkHttpWork();
            }
        }

        return instance;
    }

    /**
     * 上传信息
     * @param url
     * @param params
     */
    public void uploadConent(String url,Map<String,Object> params){
        LogUtil.i(url);
        LogUtil.i(params.toString());
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "");
            }
        }

        Request request;

        // 发起request
        request = new Request.Builder().url(url).post(builder.build()).build();
        OkHttpClient uploadHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .build();
        uploadHttpClient.newCall(request).enqueue(new MyCallback(null));
    }

    /**
     * post 请求
     *
     * @param url          url
     * @param params       参数
     * @param httpCallBack 回调
     */
    public void post( final String url, final Map<String, Object> params,  final HttpCallBack httpCallBack) {
        post(null, url, params, httpCallBack);
    }

    /**
     * post 请求
     *
     * @param context      发起请求的context
     * @param url          url
     * @param params       参数
     * @param httpCallBack 回调
     */
    public void post(Context context, final String url, final Map<String, Object> params, final HttpCallBack httpCallBack) {
        // post builder 参数
        LogUtil.i(url);
        LogUtil.i(params.toString());
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue() != null ? entry.getValue().toString() : "");
            }
        }

        Request request;

        // 发起request
        if (context == null) {
            request = new Request.Builder().url(url).post(builder.build()).build();
        } else {
            request = new Request.Builder().url(url).post(builder.build()).tag(context).build();
        }

        okHttpClient.newCall(request).enqueue(new MyCallback(httpCallBack));
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件url
     */
    public void downLoadFile(String fileUrl, final DownLoadCallBack callBack) {
        LogUtil.i("downloadBitmapFromNet", fileUrl);
        OkHttpClient downLoadClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder().url(fileUrl).build();
        downLoadClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                ThreadUtil.getInstance().runMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFail(e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] buffer = new byte[1024];
                InputStream is = response.body().byteStream();
                int len;
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                ThreadUtil.getInstance().runMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onSuccess(baos.toByteArray());
                    }
                });
                if (is != null) {
                    is.close();
                }
                if (baos != null) {
                    baos.close();
                }
            }
        });
    }

    /**
     * 回调接口
     */
    private class MyCallback implements Callback {

        private HttpCallBack mHttpCallBack;

        public MyCallback(HttpCallBack responseHandler) {
            mHttpCallBack = responseHandler;
        }

        @Override
        public void onFailure(final Call call, final IOException e) {
            LogUtil.e("onFailure", e);

            ThreadUtil.getInstance().runMainThread(new Runnable() {

                @Override
                public void run() {
                    if (mHttpCallBack != null && !call.isCanceled()) {
                        mHttpCallBack.onFail(e.toString());
                    }
                }
            });
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            if (response.isSuccessful()) {
                final String response_body = response.body().string();
                LogUtil.i(response_body);
                if (mHttpCallBack instanceof OkHttpModelCallBack) {
                    ThreadUtil.getInstance().runMainThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (mHttpCallBack != null) {
                                    mHttpCallBack.parseNetworkResponse(response_body);
                                }
                            } catch (Exception e) {
                                LogUtil.e("onResponse fail parse gson, body=" + response_body, e);
                                mHttpCallBack.onFail("parse fail");
                            }
                        }
                    });
                } else {
                    LogUtil.e("onResponse fail status=" + response.code());
                    ThreadUtil.getInstance().runMainThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mHttpCallBack != null) {
                                mHttpCallBack.onFail("fail status=" + response.code());
                            }
                        }
                    });
                }
            }else{
                LogUtil.e("onResponse fail:code:"+response.code() +"==>message:"+ response.message());
                ThreadUtil.getInstance().runMainThread(new Runnable() {

                    @Override
                    public void run() {
                        if (mHttpCallBack != null) {
                            mHttpCallBack.onFail("fail status=" + response.code());
                        }
                    }
                });
            }
        }

    }

    /**
     * 取消当前context的所有请求
     *
     * @param context
     */
    public void cancel(Context context) {
        if (okHttpClient != null) {
            for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                if (call.request().tag().equals(context))
                    call.cancel();
            }
            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                if (call.request().tag().equals(context))
                    call.cancel();
            }
        }
    }

    /**
     * 验证主机名
     */
    static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        // 信任的主机
        @Override
        public boolean verify(String hostname, SSLSession session) {
            LogUtil.i("verifyhost:" + hostname);
            // 示例
            if ("api.jrmf360.com".equals(hostname) || "api-test.jrmf360.com".equals(hostname)
                    || "yun-test.jrmf360.com".equals(hostname) || "api-collection.jrmf360.com".equals(hostname)
                    || "a.jrmf360.com".equals(hostname)) {
                return true;
            } else {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                boolean verify = hv.verify(hostname, session);
                return verify;
            }
        }
    };
}
