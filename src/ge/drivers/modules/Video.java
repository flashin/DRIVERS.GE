/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ge.drivers.app.VideoActivity;
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

    public Video(JSONObject obj, String createDate) {

        this.video = obj;
        this.folder = createDate.substring(6) + "/" + createDate.substring(3, 5) + "/";
    }

    //Returns Image view with the image
    public View getView(Context context, int width) {

        try {
            int height = (width / 16) * 9;
            final int w = width;
            final int h = height;
            final Context cont = context;
            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
            final LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            final LinearLayout container = new LinearLayout(context);
            container.setLayoutParams(lp);

            final ImageView IMG = new ImageView(context);

            String screenUrl = ServerConn.url + ServerConn.screen + folder + video.getString("video_thumbnail");
            AsyncTask loader = new AsyncTask<String, Void, LayerDrawable>() {

                private String error = null;

                @Override
                protected LayerDrawable doInBackground(String... args) {

                    try {
                        Drawable[] layers = new Drawable[2];
                        layers[0] = drawableFromUrl(args[0]);
                        layers[1] = cont.getResources().getDrawable(MyResource.getDrawable(cont, "play_button"));

                        LayerDrawable ld = new LayerDrawable(layers);
                        int lR = (int) ((w - h / 3) / 1);
                        int tB = (int) ((w - h / 2) / 2);
                        ld.setLayerInset(1, lR, tB, lR, tB);

                        return ld;
                    } catch (Exception e) {
                        error = e.toString();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(LayerDrawable result) {

                    if (result != null) {
                        IMG.setImageDrawable(result);
                        IMG.setLayoutParams(fp);

                        container.addView(IMG);
                    }
                }
            };
            loader.execute(new String[]{screenUrl});

            OnClickListener clickListener = new OnClickListener() {

                public void onClick(View v) {
                    try {
                        // the content
                        String videoUrl = folder + video.getString("video_url");

                        Intent intent = new Intent(cont, VideoActivity.class);
                        intent.putExtra("video_url", videoUrl);
                        cont.startActivity(intent);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            IMG.setOnClickListener(clickListener);

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
