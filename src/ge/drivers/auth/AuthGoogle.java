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
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import ge.drivers.app.MainActivity;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.ServerConn;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
        this.mAccountManager = AccountManager.get(activity);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts.length > 0) {
            account = accounts[0];
        }
    }

    public void googleClickCallback() {

        if (account != null) {
            try {
                /*
                 * AccountManagerFuture<Bundle> accountManagerFuture;
                 * accountManagerFuture = mAccountManager.getAuthToken(account,
                 * "android", null, activity, null, null);
                 *
                 * Bundle authTokenBundle = accountManagerFuture.getResult();
                 * token =
                 * authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN).toString();
                 * this.setInfoByToken();
                 */
                GoogleLoginer gl = new GoogleLoginer();
                gl.execute((Void) null);

            } catch (Exception e) {
                MyAlert.alertWin(activity, "Google Login Exception: " + e);
            }
        } else {
            MyAlert.alertWin(activity, "No Google account found on the device");
        }
    }

    public void setInfoByToken() {

        try {
            Map<String, Object> p = new HashMap<String, Object>();
            p.put("token", token);
            p.put("service", "GoogleService");

            JSONObject driversRes = ServerConn.postJsonSimple("login", p);

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

    private class GoogleLoginer extends AsyncTask<Object, Void, String> {

        private int start;
        private String error = null;

        @Override
        protected String doInBackground(Object... urls) {
            token = null;
            try {
                String api_scope = Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME + " email";
                //String scope = "oauth2:server:client_id:" + clientId + ":api_scope:" + api_scope;
                String scope = "oauth2:" + api_scope;
                token = GoogleAuthUtil.getToken(activity, account.name, scope);
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
        protected void onPostExecute(String result) {

            if (error != null) {
                MyAlert.alertWin(activity, "Google Login Exception: " + error);
                return;
            }
            MyAlert.alertWin(activity, token);
            if (token != null) {
                setInfoByToken();
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
