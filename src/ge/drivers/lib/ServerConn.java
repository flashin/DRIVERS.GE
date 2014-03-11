/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.webkit.MimeTypeMap;
import ge.drivers.auth.Auth;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;
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

    //Gets Json Object from url (post method) with Auth
    public static JSONObject postJson(String module, Map<String, Object> params) {

        if (params == null) {
            params = new HashMap<String, Object>();
        }

        //add login params
        if (Auth.getInstance().getUserId() > 0) {
            if (!params.containsKey("currentUserId")) {
                params.put("currentUserId", Auth.getInstance().getUserId());
            }
            if (!params.containsKey("sessionId")) {
                params.put("sessionId", Auth.getInstance().getSessionId());
            }
        }

        return postJsonSimple(module, params);
    }

    //Gets Json Object from url (post method)
    public static JSONObject postJsonSimple(String module, Map<String, Object> params) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url + api + module);

            if (params != null) {
                //set entity for file upload
                if (module.equals("uploadtemp") && params.containsKey("files")) {
                    MultipartEntity reqEntity = new MultipartEntity();
                    Iterator iterator = params.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry pairs = (Map.Entry) iterator.next();
                        if (pairs.getKey().toString().equals("files")) {
                            File f = new File(pairs.getValue().toString());
                            String extension = MimeTypeMap.getFileExtensionFromUrl(pairs.getValue().toString());
                            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            reqEntity.addPart("files[]", new FileBody(f, mime));
                        } else if (pairs.getValue() != null) {
                            reqEntity.addPart(pairs.getKey().toString(), new StringBody(pairs.getValue().toString()));
                        }
                        httpPost.setEntity(reqEntity);
                    }
                } else {
                    Iterator iterator = params.entrySet().iterator();
                    List<NameValuePair> arr = new ArrayList<NameValuePair>();
                    while (iterator.hasNext()) {
                        Map.Entry pairs = (Map.Entry) iterator.next();

                        if (pairs.getValue() != null) {
                            arr.add(new BasicNameValuePair(pairs.getKey().toString(), pairs.getValue().toString()));
                        }
                    }
                    httpPost.setEntity(new UrlEncodedFormEntity(arr, HTTP.UTF_8));
                }
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
            if (params != null) {
                Iterator iterator = params.entrySet().iterator();
                List<NameValuePair> arr = new ArrayList<NameValuePair>();
                while (iterator.hasNext()) {
                    Map.Entry pairs = (Map.Entry) iterator.next();
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

    // Creates Bitmap from InputStream and returns it
    public static Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            InputStream stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }
    
}
