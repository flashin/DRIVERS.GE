/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import ge.drivers.lib.MyAlert;
import ge.drivers.lib.ServerConn;

import android.content.Context;
import android.widget.LinearLayout;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Posts {

    private Post[] posts;

    public Posts() {

        this.posts = null;
    }

    //load posts from server
    public void loadPosts() {

        JSONObject obj = ServerConn.getJson("search");

        try {
            if (obj.getString("success").compareTo("true") == 0) {
                JSONArray jarr = obj.getJSONArray("data");
                int size = jarr.length();size=2;

                this.posts = new Post[size];
                for (int i = 0; i < size; i++) {
                    this.posts[i] = new Post(jarr.getJSONObject(i));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //draw posts
    public void drawPosts(Context context, LinearLayout lout) {

        if (posts != null) {
            for (int i = 0; i < this.posts.length; i++) {
                lout.addView(posts[i].getView(context));
            }
        }
    }
}
