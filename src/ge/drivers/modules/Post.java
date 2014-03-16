/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import ge.drivers.app.MainActivity;
import ge.drivers.app.PostActivity;
import ge.drivers.app.R;
import ge.drivers.app.UploadActivity;
import ge.drivers.auth.Auth;
import ge.drivers.auth.AuthFB;
import ge.drivers.auth.AuthGoogle;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.MyResource;
import ge.drivers.lib.TopProgressBar;
import ge.drivers.lib.ServerConn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Post {

    //Post data in jsonObject
    private int post_id = 0;
    private JSONObject post;
    private Map<String, String> textData = new HashMap<String, String>();
    private Image[] imgs;
    private Video[] videos;
    private View v = null;
    private TextView postUnlike = null;
    private TextView postLike = null;
    private Context context;
    private boolean openInner = true;

    public Post(int id, Context cont) {

        this.context = cont;
        this.openInner = false;
        this.post_id = id;
        String module = "post?id=" + id;

        final TopProgressBar pb = MyAlert.getStandardProgress(context);
        AsyncTask loader = new AsyncTask<String, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(String... args) {

                try {
                    return ServerConn.getJson(args[0]);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {

                pb.dismiss();
                try {
                    if (result != null && result.has("data")) {
                        buildData(result.getJSONObject("data"));
                        drawInnerPostView();
                    }
                } catch (Exception e) {
                    MyAlert.alertWin(context, e.toString());
                }
            }
        };
        loader.execute(new String[]{module});
    }

    public Post(JSONObject obj, Context cont) {

        this.context = cont;
        buildData(obj);
    }

    public void buildData(JSONObject obj) {

        this.post = obj;

        JSONObject jarr;
        JSONObject jobj;
        String createDate;
        int size;

        //If post has images
        try {
            createDate = obj.getString("create_date");

            if (openInner) {
                jarr = obj.getJSONObject("images");
                size = jarr.length();

                Iterator<String> iter = jarr.keys();
                this.imgs = new Image[size];
                int i = 0;
                while (iter.hasNext() && i < size) {
                    jobj = jarr.getJSONObject(iter.next());

                    this.imgs[i++] = new Image(jobj, createDate);
                }
            } else {
                JSONArray arr = obj.getJSONArray("images");
                if (arr != null) {
                    size = arr.length();
                    this.imgs = new Image[size];
                    for (int i = 0; i < size; i++) {
                        this.imgs[i] = new Image(arr.getJSONObject(i), createDate);
                    }
                }
            }
        } catch (JSONException e) {
            this.imgs = null;
        }

        //If post has videos
        try {
            createDate = obj.getString("create_date");
            if (openInner) {
                jarr = obj.getJSONObject("videos");
                size = jarr.length();
                this.videos = new Video[size];

                Iterator<String> iter = jarr.keys();
                int i = 0;
                while (iter.hasNext() && i < size) {
                    jobj = jarr.optJSONObject(iter.next());

                    this.videos[i++] = new Video(jobj, createDate);
                }
            } else {
                JSONArray arr = obj.getJSONArray("videos");
                if (arr != null) {
                    size = arr.length();
                    this.videos = new Video[size];
                    for (int i = 0; i < size; i++) {
                        this.videos[i] = new Video(arr.getJSONObject(i), createDate);
                    }
                }
            }
        } catch (JSONException e) {
            this.videos = null;
        }

        //text data
        try {
            this.post_id = post.getInt("id");
            String[] fields = {"make_name", "model_name", "plate_number", "open_comment", "user_name"};
            size = fields.length;
            for (int i = 0; i < size; i++) {
                if (post.has(fields[i]) && !post.getString(fields[i]).equals("null")) {
                    textData.put(fields[i], postStr(post.getString(fields[i])));
                } else {
                    textData.put(fields[i], "");
                }
            }
        } catch (Exception e) {
            MyAlert.alertWin(context, e.toString());
        }

        this.createView(context);
    }

    //create view
    public void createView(Context cont) {

        if (v != null) {
            return;
        }

        try {
            OnClickListener openInnerListener = null;
            if (openInner) {
                openInnerListener = new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        try {
                            Intent intent = new Intent(context, PostActivity.class);
                            intent.putExtra("id", post_id);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            MyAlert.alertWin(context, e.toString());
                        }
                    }
                };
            }

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            LinearLayout playout = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "post"), null);

            TextView postTitle = (TextView) playout.getChildAt(0);
            postTitle.setText(textData.get("make_name") + " " + textData.get("model_name") + " / " + textData.get("plate_number"));

            TextView postComment = (TextView) ((LinearLayout) playout.getChildAt(1)).getChildAt(0);
            postComment.setText(textData.get("open_comment"));

            LinearLayout actionIcon = (LinearLayout) ((LinearLayout) playout.getChildAt(1)).getChildAt(1);
            if (post.getString("is_owner").equals("1")) {
                actionIcon.addView(getDeleteIcon());
            } else {
                actionIcon.addView(getReportIcon());
            }

            LinearLayout itemsCont = (LinearLayout) playout.getChildAt(2);

            LinearLayout bottomBar = (LinearLayout) playout.getChildAt(3);
            TextView postAuthor = (TextView) bottomBar.getChildAt(0);
            postAuthor.setText(context.getString(MyResource.getString(context, "post_author")) + ": " + textData.get("user_name"));
            TextView postDate = (TextView) bottomBar.getChildAt(1);
            postDate.setText(context.getString(MyResource.getString(context, "post_date")) + ": " + post.getString("create_date"));

            if (openInnerListener != null) {
                postTitle.setClickable(true);
                postTitle.setOnClickListener(openInnerListener);

                postComment.setClickable(true);
                postComment.setOnClickListener(openInnerListener);

                postAuthor.setClickable(true);
                postAuthor.setOnClickListener(openInnerListener);

                postDate.setClickable(true);
                postDate.setOnClickListener(openInnerListener);
            }

            LinearLayout likeBar = (LinearLayout) playout.getChildAt(4);
            postUnlike = (TextView) likeBar.getChildAt(0);
            postUnlike.setClickable(true);
            postUnlike.setText(context.getString(MyResource.getString(context, "post_unlike")) + " (" + post.getString("unlike_count") + ")");
            if (Auth.getInstance().isLogged() && post.getString("like_value").equals("null")) {
                postUnlike.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String[] p = new String[1];
                        p[0] = "dislike/";
                        try {
                            p[0] = p[0] + post.getString("id");
                        } catch (Exception e) {
                            p[0] = p[0] + "0";
                        }
                        RatePostTask rt = new RatePostTask();
                        rt.execute(p);
                    }
                });
            }

            postLike = (TextView) likeBar.getChildAt(1);
            postLike.setClickable(true);
            postLike.setText(context.getString(MyResource.getString(context, "post_like")) + " (" + post.getString("like_count") + ")");
            if (Auth.getInstance().isLogged() && post.getString("like_value").equals("null")) {
                postLike.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String[] p = new String[1];
                        p[0] = "like/";
                        try {
                            p[0] = p[0] + post.getString("id");
                        } catch (Exception e) {
                            p[0] = p[0] + "0";
                        }
                        RatePostTask rt = new RatePostTask();
                        rt.execute(p);
                    }
                });
            }
            if (post.getString("like_value").compareTo("0") == 0) {
                postUnlike.setTypeface(postUnlike.getTypeface(), Typeface.BOLD);
            } else if (post.getString("like_value").compareTo("1") == 0) {
                postLike.setTypeface(postLike.getTypeface(), Typeface.BOLD);
            }

            //add images
            if (this.imgs != null) {
                for (int i = 0; i < imgs.length; i++) {
                    View im = this.imgs[i].getView(context);
                    if (openInnerListener != null) {
                        im.setOnClickListener(openInnerListener);
                    }
                    itemsCont.addView(im);
                }
            }
            //add videos
            if (this.videos != null) {
                int viewWidth = context.getResources().getDisplayMetrics().widthPixels;
                int viewHeight = context.getResources().getDisplayMetrics().heightPixels;
                if (viewHeight < viewWidth) {
                    viewWidth = viewHeight;
                }
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

    public void drawInnerPostView() {

        try {
            ListView actionsList = (ListView) ((Activity) context).findViewById(MyResource.getResource(context, "post_actions"));
            final ActionsAdapter actions = new ActionsAdapter(MyResource.getResource(context, "post_action_item"), new ArrayList<String>());
            actionsList.setAdapter(actions);

            String actionTitle = null;
            HashMap<String, String> hm = null;

            actions.add(actionTitle);
            actions.addAction(actionTitle, hm);

            if (post.getString("is_anonymous").equals("0")) {
                actionTitle = post.getString("user_name") + context.getString(MyResource.getString(context, "action_user_posts"));
                hm = new HashMap<String, String>();
                hm.put("user", post.getString("user_id"));
                actions.addAction(actionTitle, hm);
                actions.add(actionTitle);
            }

            actionTitle = post.getString("plate_number") + " " + context.getString(MyResource.getString(context, "action_plate_posts"));
            hm = new HashMap<String, String>();
            hm.put("plate", post.getString("plate_number"));
            actions.addAction(actionTitle, hm);
            actions.add(actionTitle);

            if (!post.getString("make_name").equals("null")) {
                actionTitle = post.getString("make_name") + context.getString(MyResource.getString(context, "action_car_posts"));
                hm = new HashMap<String, String>();
                hm.put("make", post.getString("make_id"));
                actions.addAction(actionTitle, hm);
                actions.add(actionTitle);
            }

            if (!post.getString("model_name").equals("null")) {
                actionTitle = post.getString("make_name") + " " + post.getString("model_name") + context.getString(MyResource.getString(context, "action_car_posts"));
                hm = new HashMap<String, String>();
                hm.put("make", post.getString("make_id"));
                hm.put("model", post.getString("model_id"));
                actions.addAction(actionTitle, hm);
                actions.add(actionTitle);
            }

            if (post.getString("city_name").equals("null")) {
                actionTitle = context.getString(MyResource.getString(context, "action_city_posts")) + " " + post.getString("city_name");
                hm = new HashMap<String, String>();
                hm.put("city", post.getString("city_id"));
                actions.addAction(actionTitle, hm);
                actions.add(actionTitle);
            }

            actionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (position > 0) {
                        Map<String, String> hm = actions.getRule(position);

                        Intent intent = new Intent(context, MainActivity.class);
                        if (!hm.isEmpty()) {
                            Iterator it = hm.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pairs = (Map.Entry) it.next();
                                intent.putExtra(pairs.getKey().toString(), pairs.getValue().toString());
                            }
                        }
                        context.startActivity(intent);
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        TopProgressBar prog_dialog = null;

        public ReportPostTask() {

            prog_dialog = MyAlert.getStandardProgress(context);
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
                    }
                    MyAlert.alertSuccessWin(context, modal_title, error);
                }
            } catch (Exception e) {
                MyAlert.alertWin(context, e.toString());
            }
        }
    }

    //get delete post icon
    public View getDeleteIcon() {

        ImageView IMG = new ImageView(context);
        Drawable dr = context.getResources().getDrawable(MyResource.getDrawable(context, "delete_post"));
        IMG.setImageDrawable(dr);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(32, 32);
        IMG.setLayoutParams(layoutParams);

        IMG.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle(context.getString(MyResource.getString(context, "delete_post_title")));
                dialog.setMessage(context.getString(MyResource.getString(context, "delete_post_question")));

                String ok_but = context.getString(MyResource.getString(context, "dialog_yes"));
                String no_but = context.getString(MyResource.getString(context, "dialog_no"));
                dialog.setPositiveButton(ok_but, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        String[] pt = new String[1];
                        try {
                            pt[0] = post.getString("id");
                        } catch (Exception e) {
                            pt[0] = "0";
                        }

                        DeletePostTask dt = new DeletePostTask();
                        dt.execute(pt);
                    }
                });
                dialog.setNegativeButton(no_but, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return IMG;
    }

    private class DeletePostTask extends AsyncTask<String, Void, JSONObject> {

        private String error = null;
        TopProgressBar prog_dialog = null;

        public DeletePostTask() {

            prog_dialog = MyAlert.getStandardProgress(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                Map<String, Object> hm = new HashMap<String, Object>();
                hm.put("id", urls[0]);

                JSONObject obj = ServerConn.postJson("deletepost", hm);
                return obj;
            } catch (Exception e) {
                error = e.toString();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            String modal_title = context.getString(MyResource.getString(context, "delete_post_title"));
            prog_dialog.dismiss();
            if (error != null) {
                MyAlert.alertWin(context, error);
                return;
            }

            try {
                if (result.has("success") && result.getString("success").equals("true")) {
                    String success_mess = context.getString(MyResource.getString(context, "delete_result"));
                    MyAlert.alertSuccessWin(context, modal_title, success_mess);
                } else {
                    error = "Unknown Error. Try Again Later";
                    if (result.has("error")) {
                        error = result.getString("error");
                    }
                    MyAlert.alertSuccessWin(context, modal_title, error);
                }
            } catch (Exception e) {
                MyAlert.alertWin(context, e.toString());
            }
        }
    }

    private class RatePostTask extends AsyncTask<String, Void, JSONObject> {

        private String error = null;

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                JSONObject obj = ServerConn.getJson(urls[0]);
                return obj;
            } catch (Exception e) {
                error = e.toString();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            if (error != null) {
                MyAlert.alertWin(context, error);
                return;
            }

            try {
                if (result.has("success") && result.getString("success").equals("true")) {
                    postUnlike.setText(context.getString(MyResource.getString(context, "post_unlike")) + " (" + result.getString("unlikes") + ")");
                    postLike.setText(context.getString(MyResource.getString(context, "post_like")) + " (" + result.getString("likes") + ")");

                    if (result.getString("action").equals("1")) {
                        postLike.setTypeface(postLike.getTypeface(), Typeface.BOLD);
                    } else {
                        postUnlike.setTypeface(postUnlike.getTypeface(), Typeface.BOLD);
                    }
                }
            } catch (Exception e) {
                MyAlert.alertWin(context, e.toString());
            }
        }
    }

    private String postStr(String str) {

        str = str.replace("\\", "");
        str = str.replace("&quot;", "\"");
        str = str.replace("&amp;", "&");
        str = str.replace("&rsquo;", "’");

        return str;
    }

    private class ActionsAdapter extends ArrayAdapter<String> {

        List<String> items = new ArrayList<String>();
        List<Map<String, String>> rules = new ArrayList<Map<String, String>>();

        public ActionsAdapter(int res, List<String> items) {

            super(context, res, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                return v;
            }

            try {
                int layoutRes = MyResource.getLayout(context, "post_action_item");
                LayoutInflater loInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = loInflater.inflate(layoutRes, parent, false);

                TextView textView = (TextView) rowView.findViewById(R.id.post_action_item);
                textView.setText(items.get(position));

                return rowView;
            } catch (Exception e) {
                return null;
            }
        }

        public void addAction(String str, Map<String, String> hm) {

            items.add(str);
            rules.add(hm);
        }

        public Map<String, String> getRule(int position) {

            return rules.get(position);
        }
    }

    public void showShareWindow() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(MyResource.getString(context, "share_title")));

        ListView lv = new ListView(context);
        final String[] items = new String[]{"Facebook", "Twitter", "Email", "SMS"};
        final int list_res = MyResource.getLayout(context, "post_share_item");

        lv.setAdapter(new ArrayAdapter<String>(context, list_res, items) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                LayoutInflater loInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = loInflater.inflate(list_res, parent, false);

                TextView textView = (TextView) rowView.findViewById(R.id.post_share_item);
                textView.setText(items[position]);

                return rowView;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    //Facebook share
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    // Add data to the intent, the receiving app will decide
                    // what to do with it.
                    share.putExtra(Intent.EXTRA_SUBJECT, "DRIVERS.GE :: " + textData.get("open_comment"));
                    share.putExtra(Intent.EXTRA_TEXT, ServerConn.url + "Post/view/" + post_id);

                    ((Activity)context).startActivity(Intent.createChooser(share, "Share..."));
                }
            }
        });

        alertDialogBuilder.setView(lv);
        alertDialogBuilder.setNegativeButton(
                "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
