/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import ge.drivers.app.UploadActivity;
import ge.drivers.lib.DynamicSpinner;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.MyResource;
import ge.drivers.lib.TopProgressBar;
import ge.drivers.lib.ServerConn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Upload {

    private List<String> items;
    private Context context;
    private int layoutRes;
    private ListView uploadList;
    private ProgressDialog dialog;
    private List<View> itemviews;
    private DynamicSpinner carMake;
    private DynamicSpinner postCity;

    public Upload(ListView uploadList, int res) {

        this.layoutRes = res;
        this.uploadList = uploadList;
        this.context = uploadList.getContext();
        this.items = new ArrayList<String>();
        this.itemviews = new ArrayList<View>();
        items.add(context.getString(MyResource.getString(context, "upload_add_item")));

        carMake = new DynamicSpinner(context, "make_id", "makes", "post_make");
        postCity = new DynamicSpinner(context, "city_id", "cities", "post_city");

        LoadFormTask ft = new LoadFormTask();
        ft.execute((Void) null);

        uploadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {
                    try {
                        deleteItem(position);
                    } catch (Exception e) {
                        MyAlert.alertWin(context, e.toString());
                    }
                }
            }
        });
    }

    private class UploadAdapter extends ArrayAdapter<String> {

        public UploadAdapter(int res) {

            super(context, res, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tp.setMargins(4, 10, 4, 10);
                LinearLayout rowView = new LinearLayout(context);
                rowView.setOrientation(LinearLayout.VERTICAL);
                rowView.setGravity(Gravity.CENTER);
                TextView uploadBut = new TextView(context);
                uploadBut.setText(items.get(position));
                uploadBut.setTextColor(Color.WHITE);
                uploadBut.setClickable(true);
                uploadBut.setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("video/*, images/*");
                        ((Activity) context).startActivityForResult(intent, 1022); //Upload activity result
                    }
                });
                uploadBut.setLayoutParams(tp);
                rowView.addView(uploadBut);
                return rowView;
            } else {
                return itemviews.get(position - 1);
            }
        }
    }

    public View getFileItemView(JSONObject obj) {

        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout ilayout = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "upload_item"), null);
            ilayout.setClickable(false);

            //Thumbnail
            ImageView IMG = new ImageView(context);
            Bitmap bm = ServerConn.downloadImage(ServerConn.url + obj.getString("thumbnailUrl"));
            IMG.setImageBitmap(bm);
            LinearLayout thumbnail = (LinearLayout) ilayout.getChildAt(0);
            thumbnail.addView(IMG);

            //File name
            TextView fname = (TextView) ilayout.getChildAt(1);
            fname.setText(obj.getString("name"));

            return ilayout;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class LoadFormTask extends AsyncTask<Object, Void, JSONObject> {

        private int start;
        private String error = null;
        private TopProgressBar prog_dialog;

        public LoadFormTask() {

            prog_dialog = MyAlert.getStandardProgress(context);
        }

        @Override
        protected JSONObject doInBackground(Object... urls) {

            try {
                JSONObject data = ServerConn.getJson("uploadtemp");
                return data;
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            prog_dialog.dismiss();

            if (error != null) {
                MyAlert.alertWin(context, error);
                return;
            }

            try {
                if (result.has("error")) {
                    MyAlert.alertWin(context, result.getString("error"));
                } else {
                    JSONArray arr = result.getJSONArray("files");
                    int size = arr.length();
                    for (int i = 0; i < size; i++) {
                        if (arr.getJSONObject(i).has("error")) {
                            MyAlert.alertWin(context, arr.getJSONObject(i).getString("error"));
                            continue;
                        }
                        items.add(arr.getJSONObject(i).getString("name"));
                        itemviews.add(getFileItemView(arr.getJSONObject(i)));
                    }
                }
                uploadList.setAdapter(new UploadAdapter(layoutRes));
            } catch (Exception e) {
                MyAlert.alertWin(context, "" + e);
            }
        }
    }

    public void uploadFile(String uri) {

        UploadFileTask uft = new UploadFileTask();
        uft.execute(new Object[]{uri});
    }

    private class UploadFileTask extends AsyncTask<Object, Integer, JSONObject> {

        private JSONObject res;
        private String error = null;

        public UploadFileTask() {
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(MyResource.getString(context, "uploading_message")));
        }

        @Override
        protected JSONObject doInBackground(Object... urls) {

            Object path = urls[0];
            try {
                Map<String, Object> hm = new HashMap<String, Object>();
                hm.put("files", path);
                res = ServerConn.postJson("uploadtemp", hm);
            } catch (Exception e) {
                error = e.toString();
            }

            return res;
        }

        @Override
        protected void onPreExecute() {

            dialog.show();
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            dialog.dismiss();

            //Error handling
            if (error != null) {
                MyAlert.alertWin(context, error);
                return;
            }

            try {
                if (result.has("error")) {
                    MyAlert.alertWin(context, result.getString("error"));
                    return;
                }

                JSONObject obj = result.getJSONArray("files").getJSONObject(0);
                if (obj.has("error")) {
                    MyAlert.alertWin(context, obj.getString("error"));
                    return;
                }
                items.add(obj.getString("name"));
                itemviews.add(getFileItemView(obj));
                uploadList.setAdapter(new UploadAdapter(layoutRes));
            } catch (Exception e) {
                MyAlert.alertWin(context, "" + e);
            }
        }
    }

    private class CreatePostTask extends AsyncTask<Object, Integer, JSONObject> {

        private JSONObject res;
        private String error = null;
        private TopProgressBar prog_dialog;

        public CreatePostTask() {
            prog_dialog = MyAlert.getStandardProgress(context);
        }

        @Override
        protected JSONObject doInBackground(Object... urls) {

            try {
                Map<String, Object> hm = new HashMap<String, Object>();

                hm.put("plate_number", getSearchParam("plate_number"));
                hm.put("make_id", getSearchParam("make_id"));
                hm.put("model_id", getSearchParam("model_id"));
                hm.put("city_id", getSearchParam("city_id"));
                hm.put("open_comment", getSearchParam("open_comment"));
                hm.put("is_anonymous", getSearchParam("is_anonymous"));

                String module = "createpost";

                return ServerConn.postJson(module, hm);
            } catch (Exception e) {
                error = e.toString();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            prog_dialog.dismiss();

            //Error handling
            if (error != null) {
                MyAlert.alertWin(context, error);
                return;
            }

            try {
                String win_title = context.getString(MyResource.getString(context, "upload_post_title"));
                String win_completed = context.getString(MyResource.getString(context, "upload_completed"));
                String win_incomplete = context.getString(MyResource.getString(context, "upload_incomplete"));

                if (result.getString("success").equals("true")) {
                    String dialog_mess = null;
                    if (result.has("completed") && result.getString("completed").equals("0")) {
                        dialog_mess = win_incomplete;
                    } else {
                        dialog_mess = win_completed;
                    }

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle(win_title);
                    alertDialogBuilder.setMessage(dialog_mess);
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            context.startActivity(new Intent(context, UploadActivity.class));
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    String error = "Unknown Error. Try Again Later";
                    if (result.has("error")) {
                        error = result.getString("error");
                    }
                    MyAlert.alertSuccessWin(context, win_title, error);
                }

            } catch (Exception e) {
                MyAlert.alertWin(context, "" + e);
            }
        }
    }

    public void deleteItem(int position) {

        String module = "deletetmpfile?file=" + items.get(position);
        String question = context.getString(MyResource.getString(context, "delete_uploaded_question"));
        String byes = context.getString(MyResource.getString(context, "dialog_yes"));
        String bno = context.getString(MyResource.getString(context, "dialog_no"));

        final String ftitle = context.getString(MyResource.getString(context, "delete_uploaded_title"));
        final String fanswer = context.getString(MyResource.getString(context, "delete_uploaded_answer"));
        final String fmod = module;
        final String fname = items.get(position);
        final int pos = position;

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(ftitle);
        dialog.setMessage(question);

        dialog.setPositiveButton(byes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                try {
                    JSONObject obj = ServerConn.postJson(fmod, null);
                    if (obj.getString(fname).equals("true")) {
                        MyAlert.alertSuccessWin(context, ftitle, fanswer);
                        items.remove(pos);
                        itemviews.remove(pos - 1);
                        uploadList.setAdapter(new UploadAdapter(layoutRes));
                        uploadList.invalidateViews();
                    } else {
                        String error = "Unknown Error";
                        if (obj.has("error")) {
                            error = obj.getString("error");
                        }
                        MyAlert.alertSuccessWin(context, ftitle, error);
                    }
                } catch (Exception e) {
                    MyAlert.alertSuccessWin(context, ftitle, e.toString());
                }
            }
        });
        dialog.setNegativeButton(bno, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void createNewPost() {

        CreatePostTask cpt = new CreatePostTask();
        cpt.execute((Void) null);
    }

    public String getSearchParam(String param) {

        if (param.equals("make_id")) {
            return carMake.getSelectedValue();
        } else if (param.equals("model_id")) {
            return carMake.getSelectedSubValue();
        } else if (param.equals("city_id")) {
            return postCity.getSelectedValue();
        } else if (param.equals("open_comment") || param.equals("plate_number")) {
            EditText txt = (EditText) ((Activity) context).findViewById(MyResource.getResource(context, param));
            String value = txt.getText().toString();
            return value;
        } else if (param.equals("is_anonymous")) {
            CheckBox ch = (CheckBox) ((Activity) context).findViewById(MyResource.getResource(context, param));
            if (ch.isChecked()) {
                return "1";
            }
            return "0";
        }

        return null;
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Video.Media.DATA};
        Cursor cursor = ((Activity) context).managedQuery(uri, projection, null, null, null);
        ((Activity) context).startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String path = cursor.getString(column_index);
        if (path == null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        }
        return path;
    }
}
