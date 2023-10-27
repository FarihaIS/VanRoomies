package com.chads.vanroomies;
import android.content.Context;
import android.content.res.Resources;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class CustomTrustManager implements X509TrustManager {
    private X509TrustManager defaultTrustManager;
    private X509Certificate[] additionalCertificates;

    public CustomTrustManager(Context context) {
        try {
            // Load our certificate from the raw directory
            Resources resources = context.getResources();
            // To-Do: Replace temporary certificate later with a VanRoomies server cert
            InputStream certInputStream = resources.openRawResource(R.raw.cert);

            // Create a CertificateFactory + read certificate
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate additionalCertificate = (X509Certificate) certificateFactory.generateCertificate(certInputStream);

            // Create a KeyStore + add additional certificate
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("my_cert", additionalCertificate);

            // Create a TrustManagerFactory with KeyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            defaultTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];

            // Store the additional certificate for reference
            additionalCertificates = new X509Certificate[]{additionalCertificate};
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        try {
            defaultTrustManager.checkClientTrusted(chain, authType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        try {
            defaultTrustManager.checkServerTrusted(chain, authType);
        } catch (Exception e) {
            // Perform additional verification if needed
            for (X509Certificate cert : chain) {
                for (X509Certificate additionalCert : additionalCertificates) {
                    try {
                        cert.verify(additionalCert.getPublicKey());
                        return; // Verified!
                    } catch (Exception ex) {
                        // Verification failed, continue checking other certificates
                    }
                }
            }
            e.printStackTrace();
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return defaultTrustManager.getAcceptedIssuers();
    }
}