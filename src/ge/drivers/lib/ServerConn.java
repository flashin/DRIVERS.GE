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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
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
        
        Map<String, Object> hm = new HashMap<String, Object>();
        
        return postJson(module, hm);
    }
    
    //Gets Json Object from url (post method)
    public static JSONObject postJson(String module, Map<String, Object> params) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url + api + module);
            if (params != null){
                Iterator iterator = params.entrySet().iterator();
                List<NameValuePair> arr = new ArrayList<NameValuePair>();
                while (iterator.hasNext()){
                    Map.Entry pairs = (Map.Entry)iterator.next();
                    arr.add(new BasicNameValuePair(pairs.getKey().toString(), pairs.getValue().toString()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(arr));
            }
            
            HttpResponse response = httpclient.execute(httpPost);
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
    
    //Gets Json Array from url
    public static JSONArray getJsonArray(String module) {
        
        Map<String, Object> hm = new HashMap<String, Object>();
        
        return postJsonArray(module, hm);
    }
    
    //Gets Json Array from url (post method)
    public static JSONArray postJsonArray(String module, Map<String, Object> params) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url + api + module);
            if (params != null){
                Iterator iterator = params.entrySet().iterator();
                List<NameValuePair> arr = new ArrayList<NameValuePair>();
                while (iterator.hasNext()){
                    Map.Entry pairs = (Map.Entry)iterator.next();
                    arr.add(new BasicNameValuePair(pairs.getKey().toString(), pairs.getValue().toString()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(arr));
            }
            
            HttpResponse response = httpclient.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();

                JSONArray JO = new JSONArray(responseString);
                return JO;
            } else {
                JSONArray JO = new JSONArray();
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
