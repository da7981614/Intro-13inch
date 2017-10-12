package com.example.xiang.intro;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfSlide extends AppCompatActivity {
    GlobalVariable G=new GlobalVariable();
    int pageid;
    int lastid;
    PDFView pdfView;
    public String pdfpath;
    Button pdfSlideNext;
    Button pdfSlideBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdfslide);
        TextView psnumber=(TextView)findViewById(R.id.psnumber);
        pdfSlideNext=(Button)findViewById(R.id.pdfSlideNext);
        pdfSlideBack=(Button)findViewById(R.id.pdfSlideBack);
        pdfView=(PDFView)findViewById(R.id.pdfView);
        G.requestQueue = Volley.newRequestQueue(getApplicationContext());
        //抓傳送的資料顯示
        Bundle bundle = this.getIntent().getExtras();
        pageid=bundle.getInt("pageid");
        lastid=bundle.getInt("lastid");
        pdfpath = bundle.getString("pdfpath");


        new RetrievePDFStream().execute("http://122.117.67.226:8857/intro/assets/upload/"+pdfpath);

        psnumber.setText(G.currentpage+"/"+G.totalpage);
        pdfSlideBack.setText("上一步");
        pdfSlideNext.setText("下一步");

        pdfSlideNext.setOnClickListener(new Button.OnClickListener() {
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
                                    Intent intent = new Intent(PdfSlide.this, LastPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else if (!imgpath.equals("")) {
                                    Intent intent = new Intent(PdfSlide.this, ImgSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("imgpath", imgpath);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!videopath.equals("")) {
                                    Intent intent = new Intent(PdfSlide.this, VideoSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("videopath", videopath);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                } else if (!pdfpath.equals("")) {
                                    Intent intent = new Intent(PdfSlide.this, PdfSlide.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("textview", textview);
                                    bundle.putString("pdfpath", pdfpath);
                                    bundle.putInt("pageid", pageid);
                                    bundle.putInt("lastid", lastid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Intent intent = new Intent(PdfSlide.this, OnlyWord.class);
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
                    Intent intent = new Intent(PdfSlide.this, LastPage.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        pdfSlideBack.setOnClickListener(new Button.OnClickListener() {
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
                                Intent intent = new Intent(PdfSlide.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (!imgpath.equals("")) {
                                Intent intent = new Intent(PdfSlide.this, ImgSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("imgpath", imgpath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else if (!videopath.equals("")) {
                                Intent intent = new Intent(PdfSlide.this, VideoSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("videopath", videopath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else if (!pdfpath.equals("")) {
                                Intent intent = new Intent(PdfSlide.this, PdfSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("pdfpath", pdfpath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();

                            } else {
                                Intent intent = new Intent(PdfSlide.this, OnlyWord.class);
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
    class RetrievePDFStream extends AsyncTask<String,Void,InputStream>{

        @Override
        protected InputStream doInBackground(String... string) {
            InputStream inputStream=null;
            try {
                URL url=new URL(string[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                Log.i("response",urlConnection.getResponseCode()+"");
                if(urlConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }
            catch(IOException e){
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream){
            pdfView.fromStream(inputStream).load();
        }
    }
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}
