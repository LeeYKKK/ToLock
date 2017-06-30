package cn.com.lyk.service;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.com.lyk.utils.SysUtils;

public class DetectorService extends Service {
    public static final String TAG = "DetectorService";
    private static  final String ACTION="com.tencent.tmgp.sgame";//王者荣耀包名


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(Intent.ACTION_MAIN);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识

                    intent.addCategory(Intent.CATEGORY_HOME);

                    startActivity(intent);
                    break;
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        getTopApp(DetectorService.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        Log.i(TAG, "Service is start.");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //停止优先级
        stopForeground(true);
        //调用广播
        Intent intent = new Intent("cn.com.lyk.detector");
        sendBroadcast(intent);
        super.onDestroy();
    }

    public void getTopApp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            if (m != null) {
                long now = System.currentTimeMillis();
                //获取60秒之内的应用数据
                List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
                Log.i(TAG, "Running app number in last 60 seconds : " + stats.size());

                String topActivity = "";

                //取得最近运行的一个app，即当前运行的app
                if ((stats != null) && (!stats.isEmpty())) {
                    int j = 0;
                    for (int i = 0; i < stats.size(); i++) {
                        if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                            j = i;
                        }
                    }
                    topActivity = stats.get(j).getPackageName();
                    if (topActivity.equals(ACTION)) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                }
                Log.i(TAG, "top running app is : " + topActivity);
            }
        }else{
            //Android版本低于5.0
            boolean isRun= SysUtils.isProessRunning(DetectorService.this,ACTION);
            if(isRun){
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
    }

}
