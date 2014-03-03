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
import com.facebook.Session;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import ge.drivers.app.MainActivity;
import ge.drivers.lib.MyAlert;
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
    private String scope = "538150776774.apps.googleusercontent.com";
    private String token = null;
    private int userId = 0;
    private String sessionId = null;
    private String email = null;

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
        AccountManager mAccountManager = AccountManager.get(activity);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts.length > 0) {
            account = accounts[0];
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

    private class GoogleLoginer extends AsyncTask<Object, Void, String> {

        private int start;
        private String error = null;

        @Override
        protected String doInBackground(Object... urls) {
            token = null;
            try {
                token = GoogleAuthUtil.getToken(activity, account.name, "oauth2:server:client_id:" + scope + ":api_scope:" + Scopes.PLUS_LOGIN);
            } catch (UserRecoverableAuthException e) {
                activity.startActivityForResult(e.getIntent(), 1);
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

            if (token != null) {
                try {
                    Map<String, Object> p = new HashMap<String, Object>();
                    p.put("token", token);
                    p.put("service", "GoogleService");

                    JSONObject driversRes = ServerConn.postJson("login", p);

                    if (driversRes.getString("success").equals("true")) {
                        //TODO Login successfull Start your next activity
                        userId = driversRes.getInt("userId");
                        sessionId = driversRes.getString("sessionId");
                        email = driversRes.getString("email");
                        activity.startActivity(new Intent(activity, MainActivity.class));
                    } else {
                        MyAlert.alertWin(activity, "Login Failed: " + driversRes.toString());
                        //Clear all session info & ask user to login again
                        Session session = Session.getActiveSession();
                        if (session != null) {
                            session.closeAndClearTokenInformation();
                        }
                    }
                } catch (Exception e) {
                    MyAlert.alertWin(activity, "Google Login Exception: " + e);
                }
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
