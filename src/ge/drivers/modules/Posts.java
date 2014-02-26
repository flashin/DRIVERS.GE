/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import ge.drivers.lib.MyAlert;
import ge.drivers.lib.ServerConn;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
    private ProgressDialog progDailog;
    private Posts self;
    private boolean allowLoading = true;

    public Posts(Context context, int res) {

        super(context, res);
        this.posts = new ArrayList<Post>();
        this.context = context;
        this.self = this;
    }

    //load posts from server
    public void loadPosts(int page) {

    	progDailog = ProgressDialog.show(context, null, "Please Wait...", true);
    	allowLoading = false;
    	PostsLoader pl = new PostsLoader();
    	pl.execute(new String[] {"search?page=" + page});
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        
        View v = posts.get(position).getView();
        return v;
    }
    
    //get loading variable
    public boolean getAllowLoading(){
    	
    	return allowLoading;
    }
    
    private class PostsLoader extends AsyncTask<String, Void, String> {
    	
    	private int start;
    	
        @Override
        protected String doInBackground(String... urls) {
        	JSONObject obj = ServerConn.getJson(urls[0]);

        	start = posts.size();
        	try {
                if (obj.getString("success").compareTo("true") == 0) {
                    JSONArray jarr = obj.getJSONArray("data");
                    int size = jarr.length();

                    Post tmp = null;                    
                    for (int i = 0; i < size; i++) {
                        tmp = new Post(jarr.getJSONObject(i), context);
                        posts.add(tmp);
                    }
                }
                
                return urls[0];
            } catch (Exception e) {
            	progDailog.dismiss();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
        	progDailog.dismiss();
        	
        	for (int i = start; i < posts.size(); i++){
        		self.add(posts.get(i));
        	}
        	
        	//If no items was returned, do not load again
        	if (start < posts.size()){
        		allowLoading = true;
        	}
        }
      }
}
