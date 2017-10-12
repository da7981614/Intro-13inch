package com.example.xiang.intro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


/**
 * Created by Xiang on 2017/7/11.
 */

public class VideoSlide extends Activity {
    GlobalVariable G=new GlobalVariable();
    int pageid;
    int lastid;
    String videopath = "";

    Button videoSlideNext;
    Button videoSlideBack;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoslide);
        TextView s3txv=(TextView)findViewById(R.id.s3txv);
        TextView vsnumber=(TextView)findViewById(R.id.vsnumber);
        videoSlideNext=(Button)findViewById(R.id.videoSlideNext);
        videoSlideBack=(Button)findViewById(R.id.videoSlideBack);
        G.requestQueue = Volley.newRequestQueue(getApplicationContext());
        //抓傳送的資料顯示
        Bundle bundle = this.getIntent().getExtras();
        s3txv.setText("再看一次");
        videopath = bundle.getString("videopath");
        pageid=bundle.getInt("pageid");
        lastid=bundle.getInt("lastid");
        watchVideo();
        vsnumber.setText(G.currentpage+"/"+G.totalpage);
        videoSlideBack.setText("上一步");
        videoSlideNext.setText("下一步");

        videoSlideNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(G.currentpage != G.totalpage-1) {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, G.showUri, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response.toString());
                            try {
                                G.currentpage++;
                                JSONArray data = response.getJSONArray("data");
                                JSONObject jasondata = data.getJSONObject(++pageid);
                                String textview = jasondata.getString("textview");
                                String wardstop= jasondata.getString("wardstop");
                                String imgpath = jasondata.getString("imgpath");
                                String videopath = jasondata.getString("videopath");
                                String pdfpath = jasondata.getString("pdfpath");
                                while(!wardstop.equals(G.checkward)&&pageid != lastid) {
                                    pageid++;
                                    jasondata = data.getJSONObject(pageid);
                                    textview = jasondata.getString("textview");
                                    wardstop= jasondata.getString("wardstop");
                                    imgpath = jasondata.getString("imgpath");
                                    videopath = jasondata.getString("videopath");
                                    pdfpath = jasondata.getString("pdfpath");
                                }
                                if(pageid == lastid){
                                    Intent intent = new Intent(VideoSlide.this, LastPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else if (!imgpath.equals("")) {
                                    Intent intent = new Intent(VideoSlide.this, ImgSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("imgpath", imgpath);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!videopath.equals("")) {
                                    Intent intent = new Intent(VideoSlide.this, VideoSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("videopath", videopath);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!pdfpath.equals("")) {
                                    Intent intent = new Intent(VideoSlide.this, PdfSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("pdfpath", pdfpath);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Intent intent = new Intent(VideoSlide.this, OnlyWord.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.append(error.getMessage());
                        }
                    });
                    G.requestQueue.add(jsonObjectRequest);
                }else{
                    Intent intent = new Intent(VideoSlide.this, LastPage.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        videoSlideBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, G.showUri, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        try {
                            G.currentpage--;
                            JSONArray data = response.getJSONArray("data");
                            JSONObject jasondata = data.getJSONObject(--pageid);
                            String textview = jasondata.getString("textview");
                            String wardstop= jasondata.getString("wardstop");
                            String imgpath = jasondata.getString("imgpath");
                            String videopath = jasondata.getString("videopath");
                            String pdfpath = jasondata.getString("pdfpath");
                            if(G.currentpage==0){
                                Intent intent = new Intent(VideoSlide.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (!imgpath.equals("")) {
                                Intent intent = new Intent(VideoSlide.this, ImgSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("imgpath", imgpath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else if (!videopath.equals("")) {
                                Intent intent = new Intent(VideoSlide.this, VideoSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("videopath", videopath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else if (!pdfpath.equals("")) {
                                Intent intent = new Intent(VideoSlide.this, PdfSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("pdfpath", pdfpath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();

                            } else {
                                Intent intent = new Intent(VideoSlide.this, OnlyWord.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.append(error.getMessage());
                    }
                });
                G.requestQueue.add(jsonObjectRequest);
            }
        });

    }
    public void SeeItAgain(View v) {
        watchVideo();
    }
    protected  void  watchVideo(){
        Intent intent = new Intent(VideoSlide.this, VideoViewer.class);
        Bundle bundle=new Bundle();
        bundle.putString("videopath", videopath);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("請問是否已經完全了解?").setTitle("導覽 - 調查")
                .setPositiveButton("完全了解", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
//                                new LongOperation().execute(BedId,MainActivity.pFileLink,MainActivity.pViewName,"3");

                    }
                })
                .setNeutralButton("部份了解", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        //        new LongOperation().execute(BedId,MainActivity.pFileLink,MainActivity.pViewName,"2");

                    }
                })
                .setNegativeButton("不了解", new DialogInterface.OnClickListener() {


                    public void onClick(DialogInterface arg0, int arg1) {
                        //        new LongOperation().execute(BedId,MainActivity.pFileLink,MainActivity.pViewName,"1");
                    }
                })
                .show();

    }
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}

