package com.example.xiang.intro;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    GlobalVariable G=new GlobalVariable();
    private WifiManager wiFiManager;
    int pageid=0;
    public int lastid;
    String BedId="";

    TextView startTxv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstpage);
        wiFiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wiFiManager.setWifiEnabled(true);
        BedId=readFromBedFile();//讀取病床號
        G.PatientLog=readFromPatientFile();//讀取病歷號

        Button startBtn=(Button)findViewById(R.id.startBtn);
        G.requestQueue = Volley.newRequestQueue(getApplicationContext());
        startTxv=(TextView)findViewById(R.id.startTxv);
        //判斷AB站
        if(Integer.parseInt(String.valueOf(BedId.subSequence(2,4)))>50){
            G.checkward=String.valueOf(BedId.subSequence(0,2))+"b";
        }
        else {
            G.checkward = String.valueOf(BedId.subSequence(0, 2)) + "b";
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, G.showUri, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
                try {
                    //抓符合當前站別的第一張導覽
                    JSONArray data = response.getJSONArray("data");
                    JSONObject jasondata = data.getJSONObject(pageid);
                    String textview = jasondata.getString("textview");
                    String slidetitle= jasondata.getString("slidetitle");
                    String slidenumber = jasondata.getString("slidenumber");
                    String wardstop = jasondata.getString("wardstop");
                    //不符合就抓下一張，抓到符合
                    while (!wardstop.equals(G.checkward)
                            &&pageid != lastid-1
                            || Integer.parseInt(slidenumber)<=0){
                        pageid++;
                        jasondata = data.getJSONObject(pageid);
                        textview = jasondata.getString("textview");
                        slidetitle= jasondata.getString("slidetitle");
                        slidenumber = jasondata.getString("slidenumber");
                        wardstop = jasondata.getString("wardstop");

                    }
                    for(int i=0;i<textview.length();i++){
                        startTxv.append(textview.charAt(i)+"");
                        if(i!=0&&i%15==0){
                            startTxv.append("\n");
                        }
                    }

                    G.finishslide=slidetitle;
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
        //點擊開始導覽鈕
        startBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, G.showUri, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        try {
                            JSONArray data = response.getJSONArray("data");
                            JSONObject jasondata = data.getJSONObject(++pageid);
                            String textview = jasondata.getString("textview");
                            String slidetitle= jasondata.getString("slidetitle");
                            String slidenumber = jasondata.getString("slidenumber");
                            String wardstop = jasondata.getString("wardstop");
                            String imgpath = jasondata.getString("imgpath");
                            String videopath = jasondata.getString("videopath");
                            String pdfpath = jasondata.getString("pdfpath");
                            //儲存整個導覽頁數，開始計算，算符合站別的導覽頁數
                            lastid=data.length();
                            G.currentpage=1;
                            int temp=0;
                            for(int i=pageid;i<data.length();i++){
                                jasondata = data.getJSONObject(i);
                                String tmp = jasondata.getString("wardstop");
                                String titletmp = jasondata.getString("slidetitle");
                                if(tmp.equals(G.checkward)) {
                                    temp++;
                                    G.finishslide=G.finishslide+" , "+titletmp;
                                }
                            }
                            G.totalpage=temp+1;
                            while (!wardstop.equals(G.checkward)
                                    &&pageid != lastid-1&& Integer.parseInt(slidenumber)<=0){
                                    pageid++;
                                    jasondata = data.getJSONObject(pageid);
                                    textview = jasondata.getString("textview");
                                    slidenumber = jasondata.getString("slidenumber");
                                    wardstop = jasondata.getString("wardstop");
                                    imgpath = jasondata.getString("imgpath");
                                    videopath = jasondata.getString("videopath");
                                    pdfpath = jasondata.getString("pdfpath");
                            }
                            //判斷
                            if(G.currentpage == G.totalpage){
                                Intent intent = new Intent(MainActivity.this, LastPage.class);
                                startActivity(intent);
                                finish();
                            } else if (!imgpath.equals("")) {
                                Intent intent = new Intent(MainActivity.this, ImgSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("imgpath", imgpath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else if (!videopath.equals("")) {
                                Intent intent = new Intent(MainActivity.this, VideoSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("videopath", videopath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            } else if (!pdfpath.equals("")) {
                                Intent intent = new Intent(MainActivity.this, PdfSlide.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("textview", textview);
                                bundle.putString("pdfpath", pdfpath);
                                bundle.putInt("pageid", pageid);
                                bundle.putInt("lastid", lastid);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();

                            } else {
                                Intent intent = new Intent(MainActivity.this, OnlyWord.class);
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
    public String readFromBedFile() {

        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,"bedlog.txt");

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            //
        }

        return text.toString();
    }
    public String readFromPatientFile() {

        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,"PatientLog.txt");

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            //
        }

        return text.toString();
    }
    //癱瘓返回鍵
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}

