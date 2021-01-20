package com.utils;

import com.Constants;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.StringJoiner;
import java.util.logging.Logger;

public final class NetUtil {

    private static final Logger LOGGER = Loggers.getLogger(NetUtil.class.getName());

    public static String doGet(String urlString) {
        var https = getHttpsConnection(urlString, "GET");
        try {
            if (https.getResponseCode() == HttpsURLConnection.HTTP_OK) { // success
                return readInputStream(https.getInputStream());
            }
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e + "\n");
        }
        throw new IllegalStateException("GET request not worked");
    }

    public static String doPost(String urlString, Map<String, String> arguments) {
        var joiner = new StringJoiner("&");
        arguments.forEach((key, value) -> joiner.add(URLEncoder.encode(key, StandardCharsets.UTF_8)
                + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8)));
        byte[] out = joiner.toString().getBytes(StandardCharsets.UTF_8);

        var https = getHttpsConnection(urlString, "POST");
        https.setFixedLengthStreamingMode(out.length);
        try {
            https.connect();
            var outputStream = https.getOutputStream();
            outputStream.write(out);
            outputStream.close();

            if (https.getResponseCode() == HttpsURLConnection.HTTP_OK) { // success
                return readInputStream(https.getInputStream());
            }
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
        }
        throw new IllegalStateException("POST request not worked");
    }

    private static HttpsURLConnection getHttpsConnection(String urlString, String method) {
        try {
            var url = new URL(urlString);
            var https = (HttpsURLConnection) url.openConnection();
//                    new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888))); // Charles
            https.setRequestMethod(method);
            if ("POST".equals(method)) {
                Constants.PROPERTIES_POST.forEach(https::setRequestProperty);
                https.setDoOutput(true);
            } else {
                Constants.PROPERTIES_GET.forEach(https::setRequestProperty);
            }
            return https;
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
            throw new IllegalStateException(e);
        }
    }

    private static String readInputStream(InputStream inputStream) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            var response = new StringBuilder();
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } catch (IOException e) {
            LOGGER.warning("IOException: " + e.getMessage() + "\n");
            throw new IllegalStateException("ReadInputStream: " + e.getMessage(), e);
        }
    }

    public static void disableVerification() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // noting
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // noting
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.warning(e.getMessage());
            e.printStackTrace();
            return;
        }

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = (hostname, session) -> true;

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

}
