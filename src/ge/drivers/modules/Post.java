/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.MyResource;

import java.util.Iterator;

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
    private View v = null;

    public Post(JSONObject obj, Context context) {

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
        
        this.createView(context);
    }

    //create view
    public void createView(Context context) {
    	
    	if (v != null){
    		return;
    	}

        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            LinearLayout playout = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "post"), null);

            TextView postTitle = (TextView)playout.getChildAt(0);
            postTitle.setText(post.getString("make_name") + " " + post.getString("model_name") + " / " + post.getString("plate_number"));
            
            TextView postComment = (TextView)playout.getChildAt(1);
            postComment.setText(post.getString("open_comment"));
            
            LinearLayout itemsCont = (LinearLayout)playout.getChildAt(2);
            
            LinearLayout bottomBar = (LinearLayout)playout.getChildAt(3);
            TextView postAuthor = (TextView)bottomBar.getChildAt(0);
            postAuthor.setText(context.getString(MyResource.getString(context, "post_author")) + ": " + post.getString("user_name"));
            TextView postDate = (TextView)bottomBar.getChildAt(1);
            postDate.setText(context.getString(MyResource.getString(context, "post_date")) + ": " + post.getString("create_date"));
            
            LinearLayout likeBar = (LinearLayout)playout.getChildAt(4);
            TextView postUnlike = (TextView)likeBar.getChildAt(0);
            postUnlike.setText(context.getString(MyResource.getString(context, "post_unlike")) + " (" + post.getString("unlike_count") + ")");
            TextView postLike = (TextView)likeBar.getChildAt(1);
            postLike.setText(context.getString(MyResource.getString(context, "post_like")) + " (" + post.getString("like_count") + ")");
            if (post.getString("like_value").compareTo("0") == 0){
                postUnlike.setTypeface(postUnlike.getTypeface(), Typeface.BOLD);
            }
            else if (post.getString("like_value").compareTo("1") == 0){
                postLike.setTypeface(postUnlike.getTypeface(), Typeface.BOLD);
            }

            //add images
            if (this.imgs != null) {
                for (int i = 0; i < imgs.length; i++) {
                    itemsCont.addView(this.imgs[i].getView(context));
                }
            }
            //add videos
            if (this.videos != null){
                int viewWidth = context.getResources().getDisplayMetrics().widthPixels;
                for (int i = 0; i < videos.length; i++){
                    itemsCont.addView(this.videos[i].getView(context, viewWidth));
                }
            }

            this.v = playout;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    //get post view
    public View getView() {
    	
    	return v;
    }
}
