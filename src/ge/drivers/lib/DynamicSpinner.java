/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.lib;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class DynamicSpinner {

    private Spinner sp;
    private ArrayAdapter<String> adapter;
    private String[] ids = null;
    private JSONArray[] subarr = null;
    private DynamicSpinner subsp = null;
    private String label;
    private Context context;

    public DynamicSpinner(Context context, String id, String module, String label) {

        sp = (Spinner) ((Activity) context).findViewById(MyResource.getResource(context, id));
        this.label = context.getString(MyResource.getString(context, label));
        this.context = context;

        adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        //Models should be stored from makes request
        if (!id.equals("model_id")) {
            StoreDataTask sdt = new StoreDataTask();
            sdt.execute(new String[]{module});
        }

        if (id.equals("make_id")) {
            //Store empty adapter to models
            subsp = new DynamicSpinner(context, "model_id", null, "post_model");

            //Set item change event
            sp.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                    subsp.storeData(subarr[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }
    }
    
    public void clearAdapter(){
    
        adapter.clear();
    }
    
    private class StoreDataTask extends AsyncTask<String, Void, JSONArray> {

        private String error = null;

        @Override
        protected JSONArray doInBackground(String... urls) {

            try {
            	JSONArray obj = ServerConn.getJsonArray(urls[0]);
                return obj;
            } catch (Exception e) {
                error = e.toString();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray result) {

            if (error != null) {
                MyAlert.alertWin(context, error);
                return;
            }

            try {
            	storeData(result);
            }
            catch (Exception e){
            	MyAlert.alertWin(context, e.toString());
            }
        }
    }

    public void storeData(JSONArray arr) {
        
        //Clear adapter before store
        if (!adapter.isEmpty()){
            adapter.clear();
        }
        
        //Select first element
        sp.setSelection(0);
        
        //if data array is empty
        if (arr == null){
            adapter.add(label);
            ids = new String[1];
            ids[0] = null;
            return;
        }

        try {
            int size = arr.length();
            ids = new String[size + 1];
            ids[0] = null;
            adapter.add(label);
            JSONObject obj;
            for (int i = 1; i < size + 1; i++) {
                obj = arr.getJSONObject(i - 1);
                ids[i] = obj.getString("id");
                adapter.add(obj.getString("name"));
                if (obj.has("models")) {
                    if (subarr == null) {
                        subarr = new JSONArray[size + 1];
                        subarr[0] = null;
                    }
                    subarr[i] = obj.getJSONArray("models");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public Spinner getSpinner(){
    
        return sp;
    }
    
    public String getSelectedValue(){
        
        int position = sp.getSelectedItemPosition();
        String value = ids[position];
        
        return value;
    }
    
    public String getSelectedSubValue(){
        
        if (subsp == null){
            return null;
        }
        
        int position = sp.getSelectedItemPosition();
        int subpos = subsp.getSpinner().getSelectedItemPosition();
        
        try {
            String value = subarr[position].getJSONObject(subpos).getString("id");
            return value;
        }
        catch (Exception e){
            return null;
        }
    }
}
