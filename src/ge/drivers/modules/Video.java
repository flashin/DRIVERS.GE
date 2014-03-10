/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnErrorListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
    private ProgressDialog progDailog = null;
    private VideoView VID = null;

    public Video(JSONObject obj, String createDate) {

        this.video = obj;
        this.folder = createDate.substring(6) + "/" + createDate.substring(3, 5) + "/";
    }

    //Returns Image view with the image
    public View getView(Context context, int width) {

        try {
            int height = (width / 16) * 9;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            LinearLayout container = new LinearLayout(context);
            container.setLayoutParams(lp);

            ImageView IMG = new ImageView(context);
            String screenUrl = ServerConn.url + ServerConn.screen + this.folder + this.video.getString("video_thumbnail");
            Drawable[] layers = new Drawable[2];
            layers[0] = this.drawableFromUrl(screenUrl);
            layers[1] = context.getResources().getDrawable(MyResource.getDrawable(context, "play_button"));

            LayerDrawable ld = new LayerDrawable(layers);
            int lR = (width - height / 2) / 2;
            int tB = (height - height / 2) / 2;
            ld.setLayerInset(1, lR, tB, lR, tB);
            IMG.setLayoutParams(fp);
            IMG.setImageDrawable(ld);

            cont = context;
            OnClickListener clickListener = new OnClickListener() {

                public void onClick(View v) {
                    try {
                    	progDailog = MyAlert.getStandardProgress(cont);
                    	
                        String videoUrl = ServerConn.url + ServerConn.video + folder + video.getString("video_url") + ".mp4";
                        
                        MediaController mediaController = new MediaController(cont);
                        VID = new VideoView(cont);
                        VID.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        VID.setMediaController(mediaController);
                        VID.setVideoPath(videoUrl);
                        VID.requestFocus();
                        
                        mediaController.show();                        
                        
                        VID.setOnPreparedListener(new OnPreparedListener() {

                            public void onPrepared(MediaPlayer mp) {
                                // TODO Auto-generated method stub
                            	progDailog.dismiss();
                            	VID.start();
                            }
                        });
                        VID.setOnErrorListener(new OnErrorListener() {

                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                // TODO Auto-generated method stub
                            	progDailog.dismiss();
                            	return false;
                            }
                        });
                        
                        Dialog dialog = new Dialog(cont);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(VID);
                        
                        dialog.show();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            IMG.setOnClickListener(clickListener);
            container.addView(IMG);

            return container;
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
