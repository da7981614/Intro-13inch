package com.example.xiang.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Xiang on 2017/8/7.
 */

public class LastPage extends Activity {
    GlobalVariable G=new GlobalVariable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lastpage);
        Button endBtn=(Button)findViewById(R.id.endBtn);
        //結束導覽時，回傳護理站別、病例號、完成事項、完成時間
        endBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t=new Thread(feedback);
                t.start();
            }
        });
    }
    private Runnable feedback=new Runnable () {
        public void run() {
            Calendar mCal = Calendar.getInstance();
            CharSequence s = DateFormat.format("yyyy-MM-dd kk:mm:ss", mCal.getTime());
            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("wardstop", G.checkward));
                params.add(new BasicNameValuePair("patientnb", G.PatientLog));
                params.add(new BasicNameValuePair("finishslide", G.finishslide));
                params.add(new BasicNameValuePair("timestamp", s+""));
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(G.PostUri);
                httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }
    };
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}