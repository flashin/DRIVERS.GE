package ge.drivers.app;

import ge.drivers.lib.MyAlert;
import ge.drivers.modules.Posts;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        try {
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
            
            setContentView(R.layout.main);

            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
            
            //load posts in the layout
            Posts posts = new Posts();
            posts.loadPosts();
            
            LinearLayout lout = (LinearLayout)findViewById(R.id.posts_area);
            posts.drawPosts(this, lout);
        }
        catch (Exception e){
            //alert exception
            MyAlert.alertWin(this, "" + e);
        }
    }
}
