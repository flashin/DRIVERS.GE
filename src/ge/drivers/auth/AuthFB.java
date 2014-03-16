/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import ge.drivers.app.MainActivity;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.ServerConn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class AuthFB {

    private static AuthFB instance = null;
    private static List<String> permissions;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private ProgressDialog dialog;
    private Activity activity;
    private int userId = 0;
    private String sessionId = null;
    private String email = null;
    private String token = null;
    private boolean block_load = false;

    private AuthFB() {
        activity = null;
    }

    public static AuthFB getInstance() {

        if (instance == null) {
            instance = new AuthFB();
        }

        return instance;
    }

    public void setFBSessionParams(Activity activity, Bundle savedInstanceState) {

        this.activity = activity;
        permissions = new ArrayList<String>();
        permissions.add("email");
        block_load = false;

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(activity, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(activity);
                token = session.getAccessToken();
            }
            Session.setActiveSession(session);
        } else {
            token = session.getAccessToken();
        }
    }

    public void fbClickCallback() {

        try {
            // TODO Check if there is any Active Session, otherwise Open New Session
            Session session = Session.getActiveSession();

            if (!session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(activity).setCallback(statusCallback).setPermissions(permissions));
            } else {
                Session.openActiveSession(activity, true, statusCallback);
            }
        } catch (Exception e) {
            MyAlert.alertWin(activity, "" + e);
        }
    }

    public void activityResultCallback(int requestCode, int resultCode, Intent data) {

        Session.getActiveSession().onActivityResult(activity, requestCode, resultCode, data);
    }

    public void saveInstanceStateCallback(Bundle outState) {

        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    public void startAppCallback() {

        Session.getActiveSession().addCallback(statusCallback);
    }

    public void stopAppCallback() {

        Session session = Session.getActiveSession();
        session.closeAndClearTokenInformation();
        session.close();

        userId = 0;
        sessionId = null;
        email = null;
    }

    private class SessionStatusCallback implements Session.StatusCallback {

        @Override
        public void call(Session session, SessionState state, Exception exception) {
            //Check if Session is Opened or not            
            processSessionStatus(session, state, exception);
        }
    }

    public void processSessionStatus(Session session, SessionState state, Exception exception) {
        
        if (session != null && session.isOpened()) {
            token = session.getAccessToken();
            if (session.getPermissions().contains("email")) {
                //Show Progress Dialog 
                dialog = new ProgressDialog(activity);
                dialog.setMessage("Loggin in..");
                dialog.show();
                block_load = true;
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                    @Override
                    public void onCompleted(GraphUser user, Response response) {

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        try {
                            if (user != null) {
                                Map<String, Object> responseMap = new HashMap<String, Object>();
                                GraphObject graphObject = response.getGraphObject();
                                responseMap = graphObject.asMap();
                                Log.i("FbLogin", "Response Map KeySet - " + responseMap.keySet());
                                // TODO : Get Email responseMap.get("email"); 

                                String fb_id = user.getId();
                                email = null;
                                String name = (String) responseMap.get("name");
                                if (responseMap.get("email") != null) {
                                    email = responseMap.get("email").toString();

                                    Map<String, Object> p = new HashMap<String, Object>();
                                    p.put("token", token);
                                    p.put("service", "FacebookService");
                                    JSONObject driversRes = ServerConn.postJsonSimple("login", p);

                                    if (driversRes.getString("success").equals("true")) {
                                        //TODO Login successfull Start your next activity
                                        userId = driversRes.getInt("userId");
                                        sessionId = driversRes.getString("sessionId");
                                        activity.startActivity(new Intent(activity, MainActivity.class));
                                    } else {
                                        MyAlert.alertWin(activity, "Login Failed");
                                        //Clear all session info & ask user to login again
                                        Session session = Session.getActiveSession();
                                        if (session != null) {
                                            session.closeAndClearTokenInformation();
                                        }
                                    }
                                } else {
                                    //Clear all session info & ask user to login again
                                    Session session = Session.getActiveSession();
                                    if (session != null) {
                                        session.closeAndClearTokenInformation();
                                    }
                                }
                            } else if (userId == 0) {
                                Map<String, Object> p = new HashMap<String, Object>();
                                p.put("token", token);
                                p.put("service", "FacebookService");
                                JSONObject driversRes = ServerConn.postJsonSimple("login", p);

                                if (driversRes.getString("success").equals("true")) {
                                    //TODO Login successfull Start your next activity
                                    userId = driversRes.getInt("userId");
                                    sessionId = driversRes.getString("sessionId");
                                    email = driversRes.getString("email");
                                    activity.startActivity(new Intent(activity, MainActivity.class));
                                } else {
                                    //Clear all session info & ask user to login again
                                    Session session = Session.getActiveSession();
                                    if (session != null) {
                                        session.closeAndClearTokenInformation();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            MyAlert.alertWin(activity, "" + e);
                        }

                        block_load = false;
                    }
                });
            } else {
                session.requestNewReadPermissions(new Session.NewPermissionsRequest(activity, permissions));
            }
        }
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

        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            return true;
        }
        return false;
    }

    public boolean isBlocked() {

        return block_load;
    }
}
