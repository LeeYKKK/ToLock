package cn.com.lyk.tolock;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import cn.com.lyk.service.TraceServiceImpl;
import cn.com.lyk.base.DaemonEnv;

/**
 * Created by LeeYK on 2017/7/12.
 */

public class App extends Application {
    @Override
    public void onCreate() {
    super.onCreate();
        DaemonEnv.initialize(this, TraceServiceImpl.class,DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        try {
            startService(new Intent(this,TraceServiceImpl.class));
        }catch (Exception e){
            Log.i("App",e.getMessage());
        }

    }
}
