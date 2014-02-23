/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.MyResource;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Post {

    //Post data in jsonObject
    private JSONObject post;
    private Image[] imgs;
    private Video[] videos;

    public Post(JSONObject obj) {

        this.post = obj;

        JSONObject jarr;
        JSONObject jobj;
        String createDate;
        int size;

        //If post has images
        try {
            createDate = obj.getString("create_date");
            jarr = obj.getJSONObject("images");
            size = jarr.length();

            Iterator<String> iter = jarr.keys();
            this.imgs = new Image[size];
            int i = 0;
            while (iter.hasNext() && i < size) {
                jobj = jarr.getJSONObject(iter.next());

                this.imgs[i++] = new Image(jobj, createDate);
            }
        } catch (JSONException e) {
            this.imgs = null;
        }

        //If post has videos
        try {
            createDate = obj.getString("create_date");
            jarr = obj.getJSONObject("videos");
            size = jarr.length();
            this.videos = new Video[size];

            Iterator<String> iter = jarr.keys();
            int i = 0;
            while (iter.hasNext() && i < size) {
                jobj = jarr.optJSONObject(iter.next());

                this.videos[i++] = new Video(jobj, createDate);
            }
        } catch (JSONException e) {
            this.videos = null;
        }
    }

    //get post view
    public View getView(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout playout = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "post"), null);

        //add images
        if (this.imgs != null) {
            for (int i = 0; i < imgs.length; i++) {
                playout.addView(this.imgs[i].getView(context));
            }
        }
        //add videos
        if (this.videos != null){
            int viewWidth = context.getResources().getDisplayMetrics().widthPixels;
            for (int i = 0; i < videos.length; i++){
                playout.addView(this.videos[i].getView(context, viewWidth));
            }
        }

        return playout;
    }
}
