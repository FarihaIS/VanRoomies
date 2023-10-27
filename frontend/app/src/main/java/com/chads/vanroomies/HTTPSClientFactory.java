package com.chads.vanroomies;

import android.content.Context;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.OkHttpClient;

public class HTTPSClientFactory {
    public static OkHttpClient createClient(Context con){
        return new OkHttpClient.Builder()
                .sslSocketFactory(createSSLSocketFactory(con), new CustomTrustManager(con))
                .build();
    }

    public static SSLSocketFactory createSSLSocketFactory(Context con) {
        try{
            SSLContext sslCon = SSLContext.getInstance("TLS");
            sslCon.init(null, null, null);
            return sslCon.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
