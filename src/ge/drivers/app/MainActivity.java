package ge.drivers.app;

import ge.drivers.modules.MyAlert;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

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
        }
        catch (Exception e){
            //alert exception
            MyAlert.alertWin(this, "" + e);
        }
    }
}
