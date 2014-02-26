package ge.drivers.app;

import ge.drivers.lib.MyAlert;
import ge.drivers.modules.Posts;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class MainActivity extends ListActivity {
    
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

            //load posts in the layout
            posts = new Posts(this, R.layout.main);
            posts.loadPosts(page++);

            setListAdapter(posts);

            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

            ListView lv = this.getListView();
            
            lv.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    
                	if (posts.getAllowLoading() && firstVisibleItem + visibleItemCount >= totalItemCount){
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
