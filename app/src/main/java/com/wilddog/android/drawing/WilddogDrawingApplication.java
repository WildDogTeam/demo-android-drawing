package com.wilddog.android.drawing;

import android.app.Application;

import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

/**
 * Created by Administrator on 2016/9/19.
 */
public class WilddogDrawingApplication extends Application{
    public void onCreate() {
        super.onCreate();
        //TODO change the url to your appurl;
        String DRAWING_URL="https://drawing.wilddogio.com";
        WilddogOptions wilddogOptions=new WilddogOptions.Builder().setSyncUrl(DRAWING_URL).build();
        WilddogApp wilddogApp=WilddogApp.initializeApp(this,wilddogOptions);
    }
}
