package com.jrmf360.neteaselib.base.utils;

import android.content.Context;

import com.jrmf360.neteaselib.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


/**
 * Https的证书管理
 * @author honglin
 *
 */
public class CertificateUtil {
	
	public static SSLSocketFactory getSocketFactory(Context context){
		SSLSocketFactory sslSocketFactory = null;
		try {
			InputStream in = context.getResources().openRawResource(R.raw.njrmf360) ;
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			X509Certificate  cer = (X509Certificate) certificateFactory.generateCertificate(in);
	        LogUtil.i("X509Certificate", cer.getSubjectDN());

	        //用我们信任的证书创建一个KeyStore
	        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	        keystore.load(null);
	        keystore.setCertificateEntry("ca", cer);

	        //创建一个TrustManagerFactory 并让TrustManagerFactory信任我们放在KeyStore中的证书
	        String algorithm = TrustManagerFactory.getDefaultAlgorithm();
	        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
	        trustManagerFactory.init(keystore);

	        //用创建好的TrustManagerFactory 来创建一个SSLContext
	        SSLContext sslContext = SSLContext.getInstance("TLS");
	        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
	        //得到SSLSocketFactory
	        sslSocketFactory = sslContext.getSocketFactory();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
        return sslSocketFactory;
	}
}
