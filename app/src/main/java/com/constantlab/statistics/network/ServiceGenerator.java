package com.constantlab.statistics.network;

import android.util.Base64;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceGenerator {


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(Constants.SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        SSLSocketFactory sslSocketFactory = null;


        try {

            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};
            final SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            sslSocketFactory = sslContext
                    .getSocketFactory();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        // Create an ssl socket factory with our all-trusting manager

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json; charset=utf-8")
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        //TODO Set Level.NONE on release
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = httpClient.addInterceptor(interceptor).readTimeout(10, TimeUnit.MINUTES).writeTimeout(10,TimeUnit.MINUTES).sslSocketFactory(sslSocketFactory).build();

        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }


}
