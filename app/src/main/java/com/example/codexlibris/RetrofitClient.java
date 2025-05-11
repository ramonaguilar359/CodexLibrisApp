package com.example.codexlibris;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            try {
                // Carrega el certificat
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream caInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.selfsigned));
                Certificate ca = cf.generateCertificate(caInput);
                caInput.close();

                // Crea un KeyStore amb el certificat
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Crea un TrustManager basat en aquest KeyStore
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);

                // Crea un SSLContext amb aquest TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                        .hostnameVerifier((hostname, session) -> hostname.equals("10.0.2.2"))
                        .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl("https://10.0.2.2/") // Port opcional si no és 443
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retrofit;
    }
}





