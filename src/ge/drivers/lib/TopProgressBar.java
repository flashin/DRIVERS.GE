/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.lib;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

/**
 *
 * @author alexx
 */
public class TopProgressBar {
    
    private ProgressBar pb = null;
    
    public TopProgressBar(Context context){
    
        try {
            pb = (ProgressBar) ((Activity)context).findViewById(MyResource.getResource(context, "top_progress"));
            pb.setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            pb = null;
            MyAlert.alertWin(context, e.toString());
        }
    }
    
    public void remove(){
        
        if (pb != null){
            pb.setVisibility(View.GONE);
        }
    }
    
    public void dismiss(){
        
        if (pb != null){
            pb.setVisibility(View.INVISIBLE);
        }
    }
    
    public void show(){
        
        if (pb != null){
            pb.setVisibility(View.VISIBLE);
        }
    }
}
