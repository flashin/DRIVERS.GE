/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import java.io.ByteArrayOutputStream;
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
public class LoadData {
    
    private static String url = "https://www.drivers.ge/API/";
    
    public static JSONObject getJson(String module){        
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url + module));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                
                JSONObject JO = new JSONObject(responseString);
                return JO;
            }
            else {
                JSONObject JO = new JSONObject();
                JO.put("success", false);
                JO.put("error", "Connection Error: " + statusLine.getStatusCode());
                return JO;
            }         
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
