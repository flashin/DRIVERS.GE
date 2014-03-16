/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import ge.drivers.auth.Auth;
import ge.drivers.lib.MyAlert;
import ge.drivers.modules.Menu;
import ge.drivers.modules.Post;

/**
 *
 * @author alexx
 */
public class PostActivity extends CommonActivity {
    
    Post post;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here  
        
        try {
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
            setContentView(R.layout.post_inner);
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_3);
            
            //Authentification
            Auth.getInstance().startAuth(this, icicle);
            
            ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
            Menu menu = new Menu(mDrawerList, R.layout.menu_item);
            
            Intent intent = getIntent();
            int id = intent.getIntExtra("id", 0);
            
            post = new Post(id, this);
        } catch (Exception e) {
            MyAlert.alertWin(this, "" + e);
        }
    }
    
    /**
     * Share pop up window
     */
    public void sharePost(View view){
    
        post.showShareWindow();
    }
}
