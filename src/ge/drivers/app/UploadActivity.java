/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import ge.drivers.auth.Auth;
import ge.drivers.lib.DynamicSpinner;
import ge.drivers.lib.MyAlert;
import ge.drivers.modules.Menu;

/**
 *
 * @author alexx
 */
public class UploadActivity extends CommonActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here     

        try {
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
            setContentView(R.layout.upload);
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

            //Authentification
            Auth.getInstance().startAuth(this, icicle);
            
            ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
            Menu menu = new Menu(mDrawerList, R.layout.menu_item);
            
            DynamicSpinner carMake = new DynamicSpinner(this, "make_id", "makes", "post_make");
            DynamicSpinner postCity = new DynamicSpinner(this, "city_id", "cities", "post_city");
        } catch (Exception e) {
            MyAlert.alertWin(this, "" + e);
        }
    }
}
