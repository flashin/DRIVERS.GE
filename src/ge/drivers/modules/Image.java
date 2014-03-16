/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        this.folder = createDate.substring(6, 10) + "/" + createDate.substring(3, 5) + "/";
    }

    //Returns Image view with the image
    public View getView(Context context) {

        final Context cont = context;
        int viewWidth = cont.getResources().getDisplayMetrics().widthPixels;
        int viewHeight = cont.getResources().getDisplayMetrics().heightPixels;
        if (viewHeight < viewWidth) {
            viewWidth = viewHeight;
        }
        final int standardWidth = viewWidth;
        viewHeight = (viewWidth / 16) * 9;

        try {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(viewWidth, viewHeight);
            final LinearLayout container = new LinearLayout(context);
            container.setLayoutParams(lp);
            
            final ImageView IMG = new ImageView(context);
            LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            IMG.setLayoutParams(fp);
            
            String img_url = ServerConn.url + ServerConn.img + folder + image.getString("image_path");
            AsyncTask loader = new AsyncTask<String, Void, Bitmap>() {

                private String error = null;

                @Override
                protected Bitmap doInBackground(String... args) {

                    try {
                        Bitmap bm = downloadImage(args[0]);
                        return bm;
                    } catch (Exception e) {
                        error = e.toString();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Bitmap result) {

                    if (result != null) {
                        float width = result.getWidth();
                        float height = result.getHeight();

                        if (width > standardWidth) {
                            height = height * (standardWidth / width);
                            width = standardWidth;
                        }

                        IMG.setImageBitmap(result);
                        container.getLayoutParams().width = (int)width;
                        container.getLayoutParams().height = (int)height;
                        container.addView(IMG);
                    }
                }
            };
            loader.execute(new String[]{img_url});

            return container;
        } catch (Exception e) {
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
