package com.example.xiang.intro;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Xiang on 2017/8/10.
 */

public class VideoViewer extends Activity{
    GlobalVariable G=new GlobalVariable();
    int pageid;
    int lastid;
    ProgressDialog pDialog;
    VideoView vv;
    String VideoURL = "";
    String videopath = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoview);
        G.requestQueue = Volley.newRequestQueue(getApplicationContext());
        Bundle bundle = this.getIntent().getExtras();
        videopath = bundle.getString("videopath");
        pageid = bundle.getInt("pageid");
        lastid = bundle.getInt("lastid");

        vv = (VideoView) findViewById(R.id.vv);
        pDialog = new ProgressDialog(this);
        // Set progressbar title
        pDialog.setTitle("導覽影片");
        // Set progressbar message
        pDialog.setMessage("影片讀取中...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        // Show progressbar
        pDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(VideoViewer.this);
            mediacontroller.setAnchorView(vv);
            // Get the URL from String VideoURL
            VideoURL = "http://122.117.67.226:8857/intro/assets/upload/" + videopath;
            if (CheckDownloadFile(VideoURL) == false) {
                Uri video = Uri.parse(VideoURL);
                vv.setMediaController(mediacontroller);
                vv.setVideoURI(video);
            } else {
                File vFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File fVideoPath = new File(vFile, videopath);
                vv.setMediaController(mediacontroller);
                vv.setVideoPath(fVideoPath.getAbsolutePath());
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        vv.requestFocus();
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                vv.start();
            }
        });
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }});
    }
    public boolean CheckDownloadFile(String fUrl)
    {
        boolean isPass=false;

        File vFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(vFile, videopath);
        if (file.exists())
        {
            Log.v("檔案已存在","導覽影片檔案已存在");
            isPass=true;
        }
        else
        {
            DownloadManager downloadManager;
            long Id=-1;
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(fUrl));

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setTitle(videopath);
            request.setDescription("下載中，請稍後");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,videopath);

            Id = downloadManager.enqueue(request);
        }

        return isPass;
    }

    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}
