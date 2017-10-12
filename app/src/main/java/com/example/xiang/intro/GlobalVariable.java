package com.example.xiang.intro;

/**
 * Created by Xiang on 2017/7/27.
 */

public class GlobalVariable {
    public String showUri = "http://122.117.67.226:8857/intro/dbconnect.php";
    public String PostUri ="http://122.117.67.226:8857/intro/finishLog.php";
    public static String PatientLog="";
    public static String finishslide="";
    public static int currentpage;
    public static int totalpage;
    public static String checkward="";
    com.android.volley.RequestQueue requestQueue;

}
