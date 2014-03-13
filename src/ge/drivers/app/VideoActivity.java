/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.ServerConn;

/**
 *
 * @author alexx
 */
public class VideoActivity extends Activity {
	
	private ProgressDialog progDialog = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.video_item);
        progDialog = MyAlert.getStandardProgress(this);
        // ToDo add your GUI initialization code here 

        try {
            Intent intent = getIntent();
            String video_url = intent.getStringExtra("video_url");

            String videoUrl = ServerConn.url + ServerConn.video + video_url + ".mp4";

            MediaController mediaController = new MediaController(this);
            final VideoView VID = new VideoView(this);
            VID.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            VID.setMediaController(mediaController);
            VID.setVideoPath(videoUrl);
            VID.requestFocus();

            mediaController.show();

            VID.setOnPreparedListener(new OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                	progDialog.dismiss();
                    VID.start();
                }
            });
            VID.setOnErrorListener(new OnErrorListener() {

                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // TODO Auto-generated method stub
                	progDialog.dismiss();
                    return false;
                }
            });
            
            LinearLayout rl = (LinearLayout) findViewById(R.id.video_cont);
            rl.addView(VID);
        } catch (Exception e) {
        	progDialog.dismiss();
            MyAlert.alertWin(this, e.toString());
        }
    }
    
    @Override
    public void onBackPressed() {
    	if (progDialog != null){
    		progDialog.dismiss();
    		finish();
    	}
		super.onBackPressed();
    }
}
