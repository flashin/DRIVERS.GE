/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import ge.drivers.app.MainActivity;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.TopProgressBar;
import ge.drivers.lib.ServerConn;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class AuthGoogle {

    private static AuthGoogle instance = null;
    private Activity activity;
    private Account account = null;
    AccountManager mAccountManager = null;
    private String clientId = "538150776774.apps.googleusercontent.com";
    private String token = null;
    private int userId = 0;
    private String sessionId = null;
    private String email = null;
    Bundle savedInstanceState;
    private boolean first_init = true;

    private AuthGoogle() {

        activity = null;
    }

    public static AuthGoogle getInstance() {

        if (instance == null) {
            instance = new AuthGoogle();
        }

        return instance;
    }

    public void setGoogleParams(Activity activity, Bundle savedInstanceState) {

        this.activity = activity;
        this.savedInstanceState = savedInstanceState;
        
        if (first_init){
            first_init = false;
            this.mAccountManager = AccountManager.get(activity);
            Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            if (accounts.length > 0) {
                account = accounts[0];
            }
        }
    }

    public void googleClickCallback() {

        if (account != null) {
            try {
                GoogleLoginer gl = new GoogleLoginer();
                gl.execute((Void) null);

            } catch (Exception e) {
                MyAlert.alertWin(activity, "Google Login Exception: " + e);
            }
        } else {
            MyAlert.alertWin(activity, "No Google account found on the device");
        }
    }

    public void setInfoByToken(JSONObject driversRes) {

        try {
        	if (driversRes.getString("success").equals("true")) {
                //TODO Login successfull Start your next activity
                userId = driversRes.getInt("userId");
                sessionId = driversRes.getString("sessionId");
                email = driversRes.getString("email");
                activity.startActivity(new Intent(activity, MainActivity.class));
            } else {
                MyAlert.alertWin(activity, "Login Failed: " + driversRes.toString());
                userId = 0;
                sessionId = null;
                email = null;
            }
        } catch (Exception e) {
            MyAlert.alertWin(activity, "Google Login Exception: " + e);
        }
    }

    private class GoogleLoginer extends AsyncTask<Object, Void, JSONObject> {

        private int start;
        private String error = null;
        private TopProgressBar prog_dialog;
        
        public GoogleLoginer(){
        	
        	prog_dialog = MyAlert.getStandardProgress(activity);
        }

        @Override
        protected JSONObject doInBackground(Object... urls) {
            token = null;
            try {
                String api_scope = Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME + " email";
                //String scope = "oauth2:server:client_id:" + clientId + ":api_scope:" + api_scope;
                String scope = "oauth2:" + api_scope;
                token = GoogleAuthUtil.getToken(activity, account.name, scope);
                
                Map<String, Object> p = new HashMap<String, Object>();
                p.put("token", token);
                p.put("service", "GoogleService");

                JSONObject driversRes = ServerConn.postJsonSimple("login", p);
                return driversRes;
            } catch (UserRecoverableAuthException e) {
                activity.startActivityForResult(e.getIntent(), 1021); //1021 google result code
            } catch (GoogleAuthException e) {
                error = e.toString();
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
        	
        	prog_dialog.dismiss();

            if (error != null) {
                MyAlert.alertWin(activity, "Google Login Exception: " + error);
                return;
            }

            if (token != null && result != null) {
                setInfoByToken(result);
            } else {
                MyAlert.alertWin(activity, "Google Access Token not found");
            }
        }
    }

    public void stopAppCallback() {

        userId = 0;
        sessionId = null;
        email = null;
    }

    public int getUserId() {

        return userId;
    }

    public String getSessionId() {

        return sessionId;
    }

    public String getEmail() {

        return email;
    }

    public boolean isLogged() {

        return getUserId() > 0;
    }
}
