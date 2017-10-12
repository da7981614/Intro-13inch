package com.example.xiang.intro;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Xiang on 2017/7/11.
 */

public class ImgSlide extends Activity {
    GlobalVariable G=new GlobalVariable();
    int pageid;
    int lastid;
    String IMGURL="";
    String BedId="";
    Button imgSlideNext;
    Button imgSlideBack;
    public String imgpath="";
    //秀圖片
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            InputStream input = conn.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(input);
            return mBitmap;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imgslide);
        TextView isnumber=(TextView)findViewById(R.id.isnumber);

        imgSlideNext=(Button)findViewById(R.id.imgSlideNext);
        imgSlideBack=(Button)findViewById(R.id.imgSlideBack);
        G.requestQueue = Volley.newRequestQueue(getApplicationContext());
        //抓傳送的資料顯示
        Bundle bundle = this.getIntent().getExtras();

        pageid=bundle.getInt("pageid");
        lastid=bundle.getInt("lastid");
        imgpath = bundle.getString("imgpath");
        isnumber.setText(G.currentpage+"/"+G.totalpage);
        imgSlideBack.setText("上一步");
        imgSlideNext.setText("下一步");

        new Thread(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                IMGURL="http://122.117.67.226:8857/intro/assets/upload/"+imgpath;
                final Bitmap mBitmap = getBitmapFromURL(IMGURL);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                                ImageView mImageView = (ImageView) findViewById(R.id.imageView);
                                mImageView.setImageBitmap(mBitmap);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }
                        // TODO Auto-generated method stub
                    }}
                );
            }}).start();
        //下一頁
        imgSlideNext.setOnClickListener(new Button.OnClickListener() {
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
                                    Intent intent = new Intent(ImgSlide.this, LastPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else if (!imgpath.equals("")) {
                                    Intent intent = new Intent(ImgSlide.this, ImgSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("imgpath", imgpath);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!videopath.equals("")) {
                                    Intent intent = new Intent(ImgSlide.this, VideoSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("videopath", videopath);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!pdfpath.equals("")) {
                                    Intent intent = new Intent(ImgSlide.this, PdfSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("pdfpath", pdfpath);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Intent intent = new Intent(ImgSlide.this, OnlyWord.class);
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
                    Intent intent = new Intent(ImgSlide.this, LastPage.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //上一頁
        imgSlideBack.setOnClickListener(new Button.OnClickListener() {
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
                                Intent intent = new Intent(ImgSlide.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (!imgpath.equals("")) {
                                Intent intent = new Intent(ImgSlide.this, ImgSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("imgpath", imgpath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else if (!videopath.equals("")) {
                                Intent intent = new Intent(ImgSlide.this, VideoSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("videopath", videopath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else if (!pdfpath.equals("")) {
                                Intent intent = new Intent(ImgSlide.this, PdfSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("pdfpath", pdfpath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();

                            } else {
                                Intent intent = new Intent(ImgSlide.this, OnlyWord.class);
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
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

}