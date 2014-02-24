/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.lib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class ServerConn {

    public static String url = "https://www.drivers.ge/";
    public static String api = "API/";
    public static String img = "media/imgs/";
    public static String video = "media/videos/";
    public static String screen = "media/post_screens/";

    //Gets Json Object from url
    public static JSONObject getJson(String module) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url + api + module));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();

                JSONObject JO = new JSONObject(responseString);
                return JO;
            } else {
                JSONObject JO = new JSONObject();
                JO.put("success", false);
                JO.put("error", "Connection Error: " + statusLine.getStatusCode());
                return JO;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Makes HttpURLConnection and returns InputStream
    public static InputStream getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }
}
