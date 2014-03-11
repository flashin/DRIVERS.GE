/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import ge.drivers.auth.AuthFB;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ge.drivers.app.MainActivity;
import ge.drivers.app.UploadActivity;

import ge.drivers.app.R;
import ge.drivers.auth.Auth;
import ge.drivers.auth.AuthGoogle;
import ge.drivers.lib.DynamicSpinner;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.MyResource;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alexx
 */
public class Menu {

    private String[] items;
    private Context context;
    private int layoutRes;
    private boolean logged;
    private DynamicSpinner make;
    private DynamicSpinner city;

    public Menu(ListView mDrawerList, int res) {
        logged = Auth.getInstance().isLogged();
        this.context = mDrawerList.getContext();
        this.layoutRes = res;
        if (logged) {
            items = new String[7];
            items[0] = Auth.getInstance().getEmail();
            items[1] = context.getString(MyResource.getString(context, "menu_videos"));
            items[2] = context.getString(MyResource.getString(context, "menu_photos"));
            items[3] = context.getString(MyResource.getString(context, "menu_upload"));
            items[4] = context.getString(MyResource.getString(context, "menu_uploaded"));
            items[5] = context.getString(MyResource.getString(context, "menu_rated"));
            items[6] = context.getString(MyResource.getString(context, "menu_logout"));
        } else {
            items = new String[4];
            items[0] = context.getString(MyResource.getString(context, "menu_videos"));
            items[1] = context.getString(MyResource.getString(context, "menu_photos"));
            items[2] = context.getString(MyResource.getString(context, "menu_facebook"));
            items[3] = context.getString(MyResource.getString(context, "menu_google"));
        }

        mDrawerList.setAdapter(new MenuAdapter(res));
        mDrawerList.setOnItemClickListener(this.getClickListener());
    }

    private class MenuAdapter extends ArrayAdapter<String> {

        public MenuAdapter(int res) {

            super(context, res, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater loInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = loInflater.inflate(layoutRes, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.menu_item);
            textView.setText(items[position]);

            return rowView;
        }
    }

    /**
     * Click event of the menu item
     */
    public AdapterView.OnItemClickListener getClickListener() {
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {

            /**
             * Opens Pop up window with law description
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Map<String, String> hm = new HashMap<String, String>();

                    if (logged) {
                        if (position == 1) {
                            hm.put("ONLY_VIDEOS", "1");
                        } else if (position == 2) {
                            hm.put("ONLY_IMAGES", "1");
                        } else if (position == 3) {
                            context.startActivity(new Intent(context, UploadActivity.class));
                        } else if (position == 4) {
                            hm.put("USER", Auth.getInstance().getUserId() + "");
                        } else if (position == 5) {
                            hm.put("MY_VOTED", "1");
                        } else if (position == 6) {
                            Auth.getInstance().destroyAuth();
                        }
                    } else {
                        if (position == 0) {
                            hm.put("ONLY_VIDEOS", "1");
                        } else if (position == 1) {
                            hm.put("ONLY_IMAGES", "1");
                        } else if (position == 2) {
                            AuthFB.getInstance().fbClickCallback();
                        } else if (position == 3) {
                            AuthGoogle.getInstance().googleClickCallback();
                        }
                    }

                    //start Main Activity again
                    if (!hm.isEmpty()) {
                        Intent intent = new Intent(context, MainActivity.class);
                        Iterator it = hm.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pairs = (Map.Entry) it.next();
                            intent.putExtra(pairs.getKey().toString(), pairs.getValue().toString());
                        }
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    MyAlert.alertWin(context, "" + e);
                }
            }
        };
        return clickListener;
    }

    public void inflateSearch() {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(MyResource.getLayout(context, "search"), null);

        LinearLayout rightDrawer = (LinearLayout) ((Activity) context).findViewById(MyResource.getResource(context, "right_drawer"));
        rightDrawer.addView(layout);

        make = new DynamicSpinner(context, "make_id", "makes", "post_make");
        city = new DynamicSpinner(context, "city_id", "cities", "post_city");
    }

    public String getSearchParam(String param) {

        if (param.equals("make_id")) {
            return make.getSelectedValue();
        } else if (param.equals("model_id")) {
            return make.getSelectedSubValue();
        } else if (param.equals("city_id")) {
            return city.getSelectedValue();
        } else if (param.equals("keyword") || param.equals("plate")) {
            EditText txt = (EditText) ((Activity) context).findViewById(MyResource.getResource(context, param));
            String value = txt.getText().toString();
            return value;
        }

        return null;
    }
}
