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
            posts.loadPosts();

            setListAdapter(posts);

            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

            ListView lv = this.getListView();
            lv.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    boolean loading = true;
                    int currentPage = 0;
                    int startingPageIndex = 0;
                    int previousTotalItemCount = 0;
                    int visibleThreshold = 5;
                    
                    // If the total item count is zero and the previous isn't, assume the
                    // list is invalidated and should be reset back to initial state
                    if (totalItemCount < previousTotalItemCount) {
                        currentPage = startingPageIndex;
                        previousTotalItemCount = totalItemCount;
                        if (totalItemCount == 0) {
                            loading = true;
                        }
                    }

                    // If it’s still loading, we check to see if the dataset count has
                    // changed, if so we conclude it has finished loading and update the current page
                    // number and total item count.
                    if (loading && (totalItemCount > previousTotalItemCount)) {
                        loading = false;
                        previousTotalItemCount = totalItemCount;
                        currentPage++;
                    }

                    // If it isn’t currently loading, we check to see if we have breached
                    // the visibleThreshold and need to reload more data.
                    // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        posts.loadPosts();
                        loading = true;
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
