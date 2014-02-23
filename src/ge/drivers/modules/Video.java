/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.drivers.modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;
import ge.drivers.lib.MyAlert;
import ge.drivers.lib.ServerConn;

import org.json.JSONObject;

/**
 *
 * @author alexx
 */
public class Video {
    
    //Video data in jsonObject
    private JSONObject video;
    private String folder;
    
    public Video(JSONObject obj, String createDate){
    
        this.video = obj;
        this.folder = createDate.substring(6) + "/" + createDate.substring(3, 5) + "/";
    }
    
    //Returns Image view with the image
    public View getView(Context context, int width) {

        try {
            MediaController mediaController = new MediaController(context);
            
            String videoUrl = ServerConn.url + ServerConn.video + this.folder + this.video.getString("video_url") + ".mp4";
            VideoView VID = new VideoView(context);
            VID.setLayoutParams(new ViewGroup.LayoutParams(width, (width / 16) * 9));
            VID.setMediaController(mediaController);
            VID.setVideoPath(videoUrl);
            
            return VID;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }        
    }
}
