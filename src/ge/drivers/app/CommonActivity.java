/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import ge.drivers.auth.Auth;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.MyResource;
import ge.drivers.modules.Menu;

/**
 *
 * @author alexx
 */
public class CommonActivity extends Activity {
    
    protected Menu menu = null;
    
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Auth.getInstance().resultAuth(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Save current session
        super.onSaveInstanceState(outState);
        Auth.getInstance().saveInstanceStateAuth(outState);
    }
    
    public void expandLeftMenu(View view){
    
        DrawerLayout drawer = (DrawerLayout)this.findViewById(MyResource.getResource(this, "drawer_layout"));
        if (drawer != null){
            drawer.openDrawer(Gravity.START);
        }
    }
    
    public void expandRightMenu(View view){
        
        DrawerLayout drawer = (DrawerLayout)this.findViewById(MyResource.getResource(this, "drawer_layout"));
        if (drawer != null){
            drawer.openDrawer(Gravity.END);
        }
    }
    
    public void submitSearch(View view){
    
        if (menu != null){
            try {
            String make = menu.getSearchParam("make_id");
            String model = menu.getSearchParam("model_id");
            String city = menu.getSearchParam("city_id");
            String keyword = menu.getSearchParam("keyword");
            String plate = menu.getSearchParam("plate");
            
            Intent intent = new Intent(this, MainActivity.class);
            if (make != null && !make.equals("0")){
                intent.putExtra("MAKE", make);
            }
            if (model != null && !model.equals("0")){
                intent.putExtra("MODEL", model);
            }
            if (city != null && !city.equals("0")){
                intent.putExtra("CITY", city);
            }
            if (keyword != null && keyword.length() > 2){
                intent.putExtra("KEYWORD", keyword);
            }
            if (plate != null && plate.length() > 2){
                intent.putExtra("PLATE", plate);
            }
            this.startActivity(intent);
            }
            catch (Exception e){
                MyAlert.alertWin(this, "" + e);
            }
        }
    }
}
