/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import ge.drivers.lib.MyAlert;
import ge.drivers.lib.ServerConn;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Posts extends ArrayAdapter<Post> {

    private ArrayList<Post> posts;
    private Context context;
    private ProgressDialog progDailog;
    private Posts self;
    private boolean allowLoading = true;
    private Map<String, Object> params;

    public Posts(Context context, int res, Intent intent) {

        super(context, res);
        this.posts = new ArrayList<Post>();
        this.context = context;
        this.self = this;
        this.params = new HashMap<String, Object>();

        if (intent != null && intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                params.put(key.toLowerCase(), intent.getExtras().getString(key));
            }
        }
    }

    //load posts from server
    public void loadPosts(int page) {

        progDailog = MyAlert.getStandardProgress(context);
        allowLoading = false;
        PostsLoader pl = new PostsLoader();
        pl.execute(new String[]{"search?page=" + page});
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = posts.get(position).getView();
        return v;
    }

    //get loading variable
    public boolean getAllowLoading() {

        return allowLoading;
    }

    private class PostsLoader extends AsyncTask<String, Void, JSONObject> {

        private int start;
        private String error = null;

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                JSONObject obj = ServerConn.postJson(urls[0], params);
                return obj;
            } catch (Exception e) {
                error = e.toString();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progDailog.dismiss();
            
            if (error != null){
                MyAlert.alertWin(context, error);
                return;
            }

            try {
                start = posts.size();
                if (result.getString("success").compareTo("true") == 0) {
                    JSONArray jarr = result.getJSONArray("data");
                    int size = jarr.length();

                    Post tmp = null;
                    for (int i = 0; i < size; i++) {
                        tmp = new Post(jarr.getJSONObject(i), context);
                        posts.add(tmp);
                    }

                    for (int i = start; i < posts.size(); i++) {
                        self.add(posts.get(i));
                    }

                    //If no items was returned, do not load again
                    if (start < posts.size()) {
                        allowLoading = true;
                    }
                }
            } catch (Exception e) {
                MyAlert.alertWin(context, e.toString());
            }
        }
    }
}
