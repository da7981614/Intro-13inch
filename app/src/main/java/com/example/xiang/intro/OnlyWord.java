package com.example.xiang.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Xiang on 2017/7/11.
 */

public class OnlyWord extends Activity{
    GlobalVariable G=new GlobalVariable();
    int pageid;
    int lastid;
    Button onlyWordNext;
    Button onlyWordBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onlyword);
        TextView s1txv=(TextView)findViewById(R.id.s1txv);
        TextView ownumber=(TextView)findViewById(R.id.ownumber);
        onlyWordNext=(Button)findViewById(R.id.onlyWordNext);
        onlyWordBack=(Button)findViewById(R.id.onlyWordBack);
        G.requestQueue = Volley.newRequestQueue(getApplicationContext());
        //抓傳送的資料顯示
        Bundle bundle = this.getIntent().getExtras();
        s1txv.setText(bundle.getString("textview"));
        pageid=bundle.getInt("pageid");
        lastid=bundle.getInt("lastid");
        ownumber.setText(G.currentpage+"/"+G.totalpage);
        onlyWordBack.setText("上一步");
        onlyWordNext.setText("下一步");
        //下一頁
        onlyWordNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(G.currentpage != G.totalpage-1){
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
                                while(!wardstop.equals(G.checkward)&&pageid != lastid-1) {
                                        pageid++;
                                        jasondata = data.getJSONObject(pageid);
                                        textview = jasondata.getString("textview");
                                        wardstop= jasondata.getString("wardstop");
                                        imgpath = jasondata.getString("imgpath");
                                        videopath = jasondata.getString("videopath");
                                        pdfpath = jasondata.getString("pdfpath");
                                }
                                if(pageid == lastid){
                                    Intent intent = new Intent(OnlyWord.this, LastPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else if (!imgpath.equals("")) {
                                    Intent intent = new Intent(OnlyWord.this, ImgSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("imgpath", imgpath);
                                    bundle.putInt("pageid",pageid);
                                    bundle.putInt("lastid",lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!videopath.equals("")) {
                                    Intent intent = new Intent(OnlyWord.this, VideoSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("videopath", videopath);
                                    bundle.putInt("pageid",pageid);
                                    bundle.putInt("lastid",lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!pdfpath.equals("")) {
                                    Intent intent = new Intent(OnlyWord.this, PdfSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("pdfpath", pdfpath);
                                    bundle.putInt("pageid",pageid);
                                    bundle.putInt("lastid",lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Intent intent = new Intent(OnlyWord.this, OnlyWord.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putInt("pageid",pageid);
                                    bundle.putInt("lastid",lastid);
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
                    Intent intent = new Intent(OnlyWord.this, LastPage.class);
                    startActivity(intent);
                    finish();
                }
            }

        });
        //上一頁
        onlyWordBack.setOnClickListener(new Button.OnClickListener() {
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
                                    Intent intent = new Intent(OnlyWord.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else if (!imgpath.equals("")) {
                                    Intent intent = new Intent(OnlyWord.this, ImgSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("imgpath", imgpath);
                                    bundle.putInt("pageid",pageid);
                                    bundle.putInt("lastid",lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!videopath.equals("")) {
                                    Intent intent = new Intent(OnlyWord.this, VideoSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("videopath", videopath);
                                    bundle.putInt("pageid",pageid);
                                    bundle.putInt("lastid",lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!pdfpath.equals("")) {
                                    Intent intent = new Intent(OnlyWord.this, PdfSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("pdfpath", pdfpath);
                                    bundle.putInt("pageid",pageid);
                                    bundle.putInt("lastid",lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Intent intent = new Intent(OnlyWord.this, OnlyWord.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putInt("pageid",pageid);
                                    bundle.putInt("lastid",lastid);
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
