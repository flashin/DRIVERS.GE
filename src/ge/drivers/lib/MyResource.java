/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.lib;

import android.content.Context;
import android.content.res.Resources;

/**
 *
 * @author alexx
 * This class is help class to work with application resources
 */
public class MyResource {
    /**
     * static method to get resource by its name
     */
    public static int getResource(Context context, String id){
        Resources R = context.getResources();
        int res = R.getIdentifier(id, "id", context.getPackageName());
        return res;
    }
    
    /**
     * static method to get layout resource by its name
     */
    public static int getLayout(Context context, String id){
        Resources R = context.getResources();
        int res = R.getIdentifier(id, "layout", context.getPackageName());
        return res;
    }
    
    /**
     * static method to get drawable resource by its name
     */
    public static int getDrawable(Context context, String id){
        Resources R = context.getResources();
        int res = R.getIdentifier(id, "drawable", context.getPackageName());
        return res;
    }
    
    /**
     * static method to get string resource by its name
     */
    public static int getString(Context context, String id){
        Resources R = context.getResources();
        int res = R.getIdentifier(id, "string", context.getPackageName());
        return res;
    }
    
    /**
     * static method to get style resource by its name
     */
    public static int getStyle(Context context, String id){
        Resources R = context.getResources();
        int res = R.getIdentifier(id, "style", context.getPackageName());
        return res;
    }
}
