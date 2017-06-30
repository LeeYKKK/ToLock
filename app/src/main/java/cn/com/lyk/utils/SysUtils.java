package cn.com.lyk.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by lyk on 2017/6/28.
 */

public class SysUtils {
    /**
     * 判断某个服务是否在运行
     * @param context
     * @param serviceName
     * serviceName 是包名+服务的类名（例如：com.beidian.test.service.BasicInfoService ）
     * @return
     */

    public static boolean isServiceWork(Context context, String serviceName){
        boolean isWork=false;
        ActivityManager myAm= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infoList=myAm.getRunningServices(100);
        if(infoList.size()<=0){
            return false;
        }
        for(int i=0;i<infoList.size();i++){
            String name =infoList.get(i).service.getClassName().toString();
            if(name.equals(serviceName)){
                isWork=true;
                break;
            }
        }
        return isWork;

    }

    /**
     * 判断进程是否在进行
     * @param context
     * @param appName
     * @return
     */

    public static boolean isProessRunning(Context context,String appName){
        boolean isRunning=false;
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos=am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info:appProcessInfos){
            if(info.processName.equals(appName)){
                isRunning=true;
            }
        }
        return isRunning;

    }

}
