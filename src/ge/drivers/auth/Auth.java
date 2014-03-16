/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import ge.drivers.app.MainActivity;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.TopProgressBar;

import ge.drivers.lib.ServerConn;

import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Auth {

    private static Auth instance = null;
    private Context context;

    private Auth() {
    }

    public static Auth getInstance() {

        if (instance == null) {
            instance = new Auth();
        }

        return instance;
    }

    public void startAuth(Activity activity, Bundle savedInstanceState) {

        this.context = activity;

        AuthFB.getInstance().setFBSessionParams(activity, savedInstanceState);
        AuthGoogle.getInstance().setGoogleParams(activity, savedInstanceState);
    }

    public void resultAuth(int requestCode, int resultCode, Intent data) {

        AuthFB.getInstance().activityResultCallback(requestCode, resultCode, data);

        if (resultCode == 1021) {
            AuthGoogle.getInstance().googleClickCallback();
        }
    }

    public void saveInstanceStateAuth(Bundle outState) {

        AuthFB.getInstance().saveInstanceStateCallback(outState);
    }

    public void startAuth() {

        AuthFB.getInstance().startAppCallback();
    }

    public void destroyAuth() {

        LogoutTask lt = new LogoutTask();
        lt.execute((Void) null);
    }

    public boolean isLogged() {

        if (AuthFB.getInstance().isLogged() || AuthGoogle.getInstance().isLogged()) {
            return true;
        }

        return false;
    }

    public int getUserId() {

        if (AuthGoogle.getInstance().getUserId() > 0) {
            return AuthGoogle.getInstance().getUserId();
        }

        return AuthFB.getInstance().getUserId();
    }

    public String getSessionId() {

        if (AuthGoogle.getInstance().getSessionId() != null) {
            return AuthGoogle.getInstance().getSessionId();
        }

        return AuthFB.getInstance().getSessionId();
    }

    public String getEmail() {

        if (AuthGoogle.getInstance().getEmail() != null) {
            return AuthGoogle.getInstance().getEmail();
        }

        return AuthFB.getInstance().getEmail();
    }

    public boolean isBlocked() {

        return AuthFB.getInstance().isBlocked();
    }

    private class LogoutTask extends AsyncTask<Object, Void, String> {

        private String error = null;
        TopProgressBar prog_dialog = null;

        public LogoutTask() {

            prog_dialog = MyAlert.getStandardProgress(context);
        }

        @Override
        protected String doInBackground(Object... urls) {

            try {
                JSONObject obj = ServerConn.getJson("logout");

                AuthFB.getInstance().stopAppCallback();
                AuthGoogle.getInstance().stopAppCallback();
                return null;
            } catch (Exception e) {
                error = e.toString();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            prog_dialog.dismiss();
            if (error != null) {
                MyAlert.alertWin(context, error);
                return;
            }

            context.startActivity(new Intent(context, MainActivity.class));
        }
    }
}
