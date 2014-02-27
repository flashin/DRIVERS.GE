/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ge.drivers.app.R;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.MyResource;

/**
 *
 * @author alexx
 */
public class Menu {

    private String[] items;
    private Context context;
    private int layoutRes;

    public Menu(ListView mDrawerList, int res) {
        boolean logged = false;
        this.context = mDrawerList.getContext();
        this.layoutRes = res;
        if (!logged) {
            items = new String[4];
            items[0] = context.getString(MyResource.getString(context, "menu_videos"));
            items[1] = context.getString(MyResource.getString(context, "menu_photos"));
            items[2] = context.getString(MyResource.getString(context, "menu_facebook"));
            items[3] = context.getString(MyResource.getString(context, "menu_google"));
        } else {
            items = null;
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
    public AdapterView.OnItemClickListener getClickListener(){
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            /**
             * Opens Pop up window with law description
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyAlert.alertWin(context, position + "");
            }
        };
        return clickListener;
    }
}
