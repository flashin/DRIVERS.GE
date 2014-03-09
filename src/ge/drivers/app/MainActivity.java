package ge.drivers.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import ge.drivers.auth.Auth;
import ge.drivers.lib.MyAlert;
import ge.drivers.modules.Menu;
import ge.drivers.modules.Posts;

public class MainActivity extends CommonActivity {

    private Posts posts;
    private int page = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
            setContentView(R.layout.main);
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

            //Authentification
            Auth.getInstance().startAuth(this, savedInstanceState);
            init_wall();
        } catch (Exception e) {
            //alert exception
            MyAlert.alertWin(this, "" + e);
        }
    }
    
    public void init_wall(){
    
        try {
            if (Auth.getInstance().isBlocked()){
                return;
            }
            
            ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
            menu = new Menu(mDrawerList, R.layout.menu_item);
            //Add Search Menu
            menu.inflateSearch();

            ListView lv = (ListView) findViewById(R.id.list);
            
            Intent intent = getIntent();

            //load posts in the layout
            posts = new Posts(this, R.layout.post, intent);
            posts.loadPosts(page++);

            lv.setAdapter(posts);

            lv.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    if (posts.getAllowLoading() && firstVisibleItem + visibleItemCount >= totalItemCount) {
                        posts.loadPosts(page++);
                    }
                }

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // Don't take any action on changed
                }
            });
        } catch (Exception e) {
            //alert exception
            MyAlert.alertWin(this, "" + e);
        }
    }
}
