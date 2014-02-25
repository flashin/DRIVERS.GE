/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import ge.drivers.lib.MyAlert;
import ge.drivers.lib.ServerConn;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Posts extends ArrayAdapter<Post> {

    private ArrayList<Post> posts;
    private Context context;

    public Posts(Context context, int res) {

        super(context, res);
        this.posts = new ArrayList<Post>();
        this.context = context;
    }

    //load posts from server
    public void loadPosts() {

        JSONObject obj = ServerConn.getJson("search");

        try {
            if (obj.getString("success").compareTo("true") == 0) {
                JSONArray jarr = obj.getJSONArray("data");
                int size = jarr.length();

                Post tmp = null;
                for (int i = 0; i < size; i++) {
                    tmp = new Post(jarr.getJSONObject(i));
                    this.posts.add(tmp);
                    this.add(tmp);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        
        View v = posts.get(position).getView(context);
        return v;
    }
}
