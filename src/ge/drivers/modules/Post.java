/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.MyResource;
import ge.drivers.lib.ServerConn;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Post {

    //Post data in jsonObject
    private JSONObject post;
    private Image[] imgs;
    private Video[] videos;
    private View v = null;
    private Context context;

    public Post(JSONObject obj, Context context) {

        this.post = obj;
        this.context = context;

        JSONObject jarr;
        JSONObject jobj;
        String createDate;
        int size;

        //If post has images
        try {
            createDate = obj.getString("create_date");
            jarr = obj.getJSONObject("images");
            size = jarr.length();

            Iterator<String> iter = jarr.keys();
            this.imgs = new Image[size];
            int i = 0;
            while (iter.hasNext() && i < size) {
                jobj = jarr.getJSONObject(iter.next());

                this.imgs[i++] = new Image(jobj, createDate);
            }
        } catch (JSONException e) {
            this.imgs = null;
        }

        //If post has videos
        try {
            createDate = obj.getString("create_date");
            jarr = obj.getJSONObject("videos");
            size = jarr.length();
            this.videos = new Video[size];

            Iterator<String> iter = jarr.keys();
            int i = 0;
            while (iter.hasNext() && i < size) {
                jobj = jarr.optJSONObject(iter.next());

                this.videos[i++] = new Video(jobj, createDate);
            }
        } catch (JSONException e) {
            this.videos = null;
        }

        this.createView(context);
    }

    //create view
    public void createView(Context context) {

        if (v != null) {
            return;
        }

        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            LinearLayout playout = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "post"), null);

            TextView postTitle = (TextView) playout.getChildAt(0);
            postTitle.setText(post.getString("make_name") + " " + post.getString("model_name") + " / " + post.getString("plate_number"));

            TextView postComment = (TextView) ((LinearLayout) playout.getChildAt(1)).getChildAt(0);
            postComment.setText(post.getString("open_comment"));

            LinearLayout actionIcon = (LinearLayout) ((LinearLayout) playout.getChildAt(1)).getChildAt(1);
            actionIcon.addView(getReportIcon());

            LinearLayout itemsCont = (LinearLayout) playout.getChildAt(2);

            LinearLayout bottomBar = (LinearLayout) playout.getChildAt(3);
            TextView postAuthor = (TextView) bottomBar.getChildAt(0);
            postAuthor.setText(context.getString(MyResource.getString(context, "post_author")) + ": " + post.getString("user_name"));
            TextView postDate = (TextView) bottomBar.getChildAt(1);
            postDate.setText(context.getString(MyResource.getString(context, "post_date")) + ": " + post.getString("create_date"));

            LinearLayout likeBar = (LinearLayout) playout.getChildAt(4);
            TextView postUnlike = (TextView) likeBar.getChildAt(0);
            postUnlike.setText(context.getString(MyResource.getString(context, "post_unlike")) + " (" + post.getString("unlike_count") + ")");
            TextView postLike = (TextView) likeBar.getChildAt(1);
            postLike.setText(context.getString(MyResource.getString(context, "post_like")) + " (" + post.getString("like_count") + ")");
            if (post.getString("like_value").compareTo("0") == 0) {
                postUnlike.setTypeface(postUnlike.getTypeface(), Typeface.BOLD);
            } else if (post.getString("like_value").compareTo("1") == 0) {
                postLike.setTypeface(postUnlike.getTypeface(), Typeface.BOLD);
            }

            //add images
            if (this.imgs != null) {
                for (int i = 0; i < imgs.length; i++) {
                    itemsCont.addView(this.imgs[i].getView(context));
                }
            }
            //add videos
            if (this.videos != null) {
                int viewWidth = context.getResources().getDisplayMetrics().widthPixels;
                for (int i = 0; i < videos.length; i++) {
                    itemsCont.addView(this.videos[i].getView(context, viewWidth));
                }
            }

            this.v = playout;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //get post view
    public View getView() {

        return v;
    }

    //get report post icon
    public View getReportIcon() {

        ImageView IMG = new ImageView(context);
        Drawable dr = context.getResources().getDrawable(MyResource.getDrawable(context, "report_post"));
        IMG.setImageDrawable(dr);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(32, 32);
        IMG.setLayoutParams(layoutParams);

        IMG.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getString(MyResource.getString(context, "report_title")));

                final EditText et = new EditText(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 90, Gravity.TOP);
                et.setLayoutParams(layoutParams);
                et.setHint(context.getString(MyResource.getString(context, "report_hint")));

                dialog.setView(et);
                String ok_but = context.getString(MyResource.getString(context, "report_send"));
                dialog.setPositiveButton(ok_but, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        String[] pt = new String[2];
                        try {
                            pt[0] = post.getString("id");
                        } catch (Exception e) {
                            pt[0] = "0";
                        }
                        pt[1] = et.getText().toString();

                        ReportPostTask rt = new ReportPostTask();
                        rt.execute(pt);
                    }
                });
                dialog.show();
            }
        });

        return IMG;
    }

    private class ReportPostTask extends AsyncTask<String, Void, JSONObject> {

        private String error = null;
        ProgressDialog prog_dialog = null;
        
        public ReportPostTask(){
        
            prog_dialog = MyAlert.getStandardProgress(context);
            prog_dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                Map<String, Object> hm = new HashMap<String, Object>();
                hm.put("id", urls[0]);
                hm.put("reportComment", urls[1]);

                JSONObject obj = ServerConn.postJson("report", hm);
                return obj;
            } catch (Exception e) {
                error = e.toString();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            String modal_title = context.getString(MyResource.getString(context, "report_title"));
            prog_dialog.dismiss();
            if (error != null) {
                MyAlert.alertWin(context, error);
                return;
            }

            try {
                if (result.has("success") && result.getString("success").equals("true")) {
                    String success_mess = context.getString(MyResource.getString(context, "report_result"));
                    MyAlert.alertSuccessWin(context, modal_title, success_mess);
                } else {
                    error = "Unknown Error. Try Again Later";
                    if (result.has("error")) {
                        error = result.getString("error");
                        MyAlert.alertSuccessWin(context, modal_title, error);
                    }
                }
            } catch (Exception e) {
                MyAlert.alertWin(context, e.toString());
            }
        }
    }
}
