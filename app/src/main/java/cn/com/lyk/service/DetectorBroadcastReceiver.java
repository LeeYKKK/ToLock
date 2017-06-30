package cn.com.lyk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by LeeYK on 2017/6/30.
 */

public class DetectorBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION ="cn.com.lyk.detector";
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "启动service", Toast.LENGTH_SHORT).show();

        if (intent.getAction().equals(ACTION)){
            startUpService(context);
        }
    }
    //启动service
    private void startUpService(Context context){
        Intent intent1=new Intent (context,DetectorService.class);
        context.startService(intent1);

    }
}
