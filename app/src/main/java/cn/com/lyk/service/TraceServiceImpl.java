package cn.com.lyk.service;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.com.lyk.base.AbsWorkService;
import cn.com.lyk.utils.SysUtils;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by LeeYK on 2017/7/13.
 */

public class TraceServiceImpl extends AbsWorkService {

    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Disposable sDisposable;
    public static final String TAG = "TraceServiceImpl";
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

    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) sDisposable.dispose();
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        getTopApp(TraceServiceImpl.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        Log.i(TAG, "Service is start.");
    }

    /**
     * 是否 任务完成, 不再需要服务运行?
     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {
        System.out.println("检查磁盘中是否有上次销毁时保存的数据");
        sDisposable = Flowable
                .interval(3, TimeUnit.SECONDS)
                //取消任务时取消定时唤醒
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("保存数据到磁盘。");
                        cancelJobAlarmSub();
                    }
                }).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) throws Exception {
                        System.out.println("每 3 秒采集一次数据... count = " + count);
                        if (count > 0 && count % 18 == 0) System.out.println("保存数据到磁盘。 saveCount = " + (count / 18 - 1));
                    }
                });
    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();
    }

    /**
     * 任务是否正在运行?
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable.isDisposed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        System.out.println("保存数据到磁盘。");
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
            boolean isRun= SysUtils.isProessRunning(TraceServiceImpl.this,ACTION);
            if(isRun){
                Message msg = Message.obtain();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
    }
}
