/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import ge.drivers.lib.MyAlert;

import java.io.IOException;
import java.io.InputStream;
import org.json.JSONObject;

import ge.drivers.lib.ServerConn;

/**
 *
 * @author alexx
 */
public class Image {

    //Image data in jsonObject
    private JSONObject image;
    private String folder;

    public Image(JSONObject obj, String createDate) {

        this.image = obj;
        this.folder = createDate.substring(6) + "/" + createDate.substring(3, 5) + "/";
    }

    //Returns Image view with the image
    public View getView(Context context) {

        try {
            ImageView IMG = new ImageView(context);
            Bitmap bm = downloadImage(ServerConn.url + ServerConn.img + this.folder + this.image.getString("image_path"));
            IMG.setImageBitmap(bm);
            
            return IMG;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }        
    }

    // Creates Bitmap from InputStream and returns it
    private Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            InputStream stream = ServerConn.getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }
}
