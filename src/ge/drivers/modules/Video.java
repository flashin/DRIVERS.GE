/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.MyResource;
import ge.drivers.lib.ServerConn;
import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Video {

    //Video data in jsonObject
    private JSONObject video;
    private String folder;
    private Context cont = null;

    public Video(JSONObject obj, String createDate) {

        this.video = obj;
        this.folder = createDate.substring(6) + "/" + createDate.substring(3, 5) + "/";
    }

    //Returns Image view with the image
    public View getView(Context context, int width) {

        try {
            int height = (width / 16) * 9;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);

            ImageView IMG = new ImageView(context);
            String screenUrl = ServerConn.url + ServerConn.screen + this.folder + this.video.getString("video_thumbnail");
            Drawable[] layers = new Drawable[2];
            layers[0] = this.drawableFromUrl(screenUrl);
            layers[1] = context.getResources().getDrawable(MyResource.getDrawable(context, "play_button"));

            LayerDrawable ld = new LayerDrawable(layers);
            int imageHeight = lp.height;
            int imageWidth = lp.width;
            int overlayHeight = layers[1].getIntrinsicHeight();
            int overlayWidth = layers[1].getIntrinsicWidth();
            int lR = (imageWidth - overlayWidth) / 2;
            int top = imageHeight - (overlayHeight + 10);
            int bottom = 10;
            ld.setLayerInset(1, lR, top, lR, bottom);
            IMG.setLayoutParams(lp);
            IMG.setImageDrawable(ld);

            cont = context;
            OnClickListener clickListener = new OnClickListener() {

                public void onClick(View v) {
                    try {
                        String videoUrl = ServerConn.url + ServerConn.video + folder + video.getString("video_url") + ".mp4";
                        
                        MediaController mediaController = new MediaController(cont);
                        VideoView VID = new VideoView(cont);
                        VID.setLayoutParams(new ViewGroup.LayoutParams(v.getWidth(), v.getHeight()));
                        VID.setMediaController(mediaController);
                        VID.setVideoPath(videoUrl);
                        VID.start();
                        
                        ViewGroup vg = (ViewGroup)v.getParent();
                        vg.removeView(v);
                        vg.addView(VID);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            IMG.setOnClickListener(clickListener);

            return IMG;
            /*
             * MediaController mediaController = new MediaController(context);
             *
             * String videoUrl = ServerConn.url + ServerConn.video + this.folder
             * + this.video.getString("video_url") + ".mp4"; VideoView VID = new
             * VideoView(context); VID.setLayoutParams(new
             * ViewGroup.LayoutParams(width, (width / 16) * 9));
             * VID.setMediaController(mediaController);
             * VID.setVideoPath(videoUrl);
             *
             * return VID;
             */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Creates Drawable from InputStream and returns it
    public Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }
}
