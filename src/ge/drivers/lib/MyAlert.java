/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.lib;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 *
 * @author alexx
 * This class is to alert error messages
 */
public class MyAlert {
    /**
     * Static method to alert message in alert dialog
     */
    public static void alertWin(Context context, String str){
        /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Error Alert");
        alertDialogBuilder.setMessage(str);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){  
                    public void onClick(DialogInterface dialog, int id) {  
                        dialog.dismiss(); 
                    }  
                });
        
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();*/
    }
    
    public static void alertSuccessWin(Context context, String title, String str){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(str);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){  
                    public void onClick(DialogInterface dialog, int id) {  
                        dialog.dismiss(); 
                    }  
                });
        
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    
    public static TopProgressBar getStandardProgress(Context context){
    
        return new TopProgressBar(context);
    }
}
