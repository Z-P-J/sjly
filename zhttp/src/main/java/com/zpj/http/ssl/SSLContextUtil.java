/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zpj.http.ssl;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by lyy on 2017/1/11.
 * SSL证书工具
 */
public class SSLContextUtil {

  public static String CA_PATH, CA_ALIAS;

  /**
   * 颁发服务器证书的 CA 未知
   *
   * @param caAlias CA证书别名
   * @param caPath 保存在assets目录下的CA证书完整路径
   */
  public static SSLContext getSSLContext(Context context, String caAlias, String caPath) {
    if (TextUtils.isEmpty(caAlias) || TextUtils.isEmpty(caPath)) {
      return null;
    }
    // Load CAs from an InputStream
    // (could be from a resource or ByteArrayInputStream or ...)
    CertificateFactory cf = null;
    try {
      cf = CertificateFactory.getInstance("X.509");
      InputStream caInput = context.getAssets().open(caPath);
      Certificate ca;
      ca = cf.generateCertificate(caInput);
      System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

      // Create a KeyStore containing our trusted CAs
      String keyStoreType = KeyStore.getDefaultType();
      KeyStore keyStore = KeyStore.getInstance(keyStoreType);
      keyStore.load(null, null);
      keyStore.setCertificateEntry(caAlias, ca);

      // Create a TrustManager that trusts the CAs in our KeyStore
      String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
      TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
      tmf.init(keyStore);
      KeyManagerFactory kmf =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(keyStore, null);

      // Create an SSLContext that uses our TrustManager
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
      return sslContext;
    } catch (CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException | KeyManagementException | UnrecoverableKeyException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 服务器证书不是由 CA 签署的，而是自签署时，获取默认的SSL
   */
  public static SSLContext getDefaultSLLContext() {
    SSLContext sslContext = null;
    try {
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[] { trustManagers }, new SecureRandom());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sslContext;
  }

  /**
   * 创建自己的 TrustManager，这次直接信任服务器证书。这种方法具有前面所述的将应用与证书直接关联的所有弊端，但可以安全地操作。
   */
  private static TrustManager trustManagers = new HTTPSTrustManager() {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  };

  public static final HostnameVerifier HOSTNAME_VERIFIER = new HostnameVerifier() {

    @Override
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }

  };
}
