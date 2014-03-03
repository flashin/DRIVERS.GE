/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ge.drivers.lib.ServerConn;

import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Auth {

    private static Auth instance = null;

    private Auth() {
        
    }

    public static Auth getInstance() {

        if (instance == null) {
            instance = new Auth();
        }

        return instance;
    }

    public void startAuth(Activity activity, Bundle savedInstanceState) {

        AuthFB.getInstance().setFBSessionParams(activity, savedInstanceState);
        AuthGoogle.getInstance().setGoogleParams(activity, savedInstanceState);
    }

    public void resultAuth(int requestCode, int resultCode, Intent data) {

        AuthFB.getInstance().activityResultCallback(requestCode, resultCode, data);
    }

    public void saveInstanceStateAuth(Bundle outState) {

        AuthFB.getInstance().saveInstanceStateCallback(outState);
    }

    public void startAuth() {

        AuthFB.getInstance().startAppCallback();
    }

    public void destroyAuth() {

        JSONObject obj = ServerConn.getJson("logout");

        AuthFB.getInstance().stopAppCallback();
        AuthGoogle.getInstance().stopAppCallback();
    }

    public boolean isLogged() {

        if (AuthFB.getInstance().isLogged() || AuthGoogle.getInstance().isLogged()) {
            return true;
        }

        return false;
    }
    
    public int getUserId() {

        if (AuthGoogle.getInstance().getUserId() > 0){
            return AuthGoogle.getInstance().getUserId();
        }
        
        return AuthFB.getInstance().getUserId();
    }

    public String getSessionId() {

        if (AuthGoogle.getInstance().getSessionId() != null){
            return AuthGoogle.getInstance().getSessionId();
        }
        
        return AuthFB.getInstance().getSessionId();
    }

    public String getEmail() {
        
        if (AuthGoogle.getInstance().getEmail() != null){
            return AuthGoogle.getInstance().getEmail();
        }

        return AuthFB.getInstance().getEmail();
    }
}
