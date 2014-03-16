/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import ge.drivers.auth.Auth;
import ge.drivers.lib.MyAlert;
import ge.drivers.modules.Menu;
import ge.drivers.modules.Upload;

/**
 *
 * @author alexx
 */
public class UploadActivity extends CommonActivity {

    private Upload upload;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // ToDo add your GUI initialization code here     

        try {
            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
            setContentView(R.layout.upload);
            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_2);

            //Authentification
            Auth.getInstance().startAuth(this, icicle);

            ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);
            Menu menu = new Menu(mDrawerList, R.layout.menu_item);

            ListView uploadList = (ListView) findViewById(R.id.upload_list);
            upload = new Upload(uploadList, R.layout.upload_item);
        } catch (Exception e) {
            MyAlert.alertWin(this, "" + e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1022 && data != null) {
            try {
                String path = upload.getPath(data.getData());
                if (path != null) {
                    upload.uploadFile(path);
                } else {
                    String error_title = this.getString(R.string.upload_file_error);
                    String error_desc = this.getString(R.string.upload_file_error_desc);
                    MyAlert.alertSuccessWin(this, error_title, error_desc);
                }
            } catch (Exception e) {
                MyAlert.alertWin(this, e.toString());
            }
        }
    }

    public void createNewPost(View view) {

        upload.createNewPost();
    }
}
