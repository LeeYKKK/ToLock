package cn.com.lyk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class DetectorService2 extends Service {

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //实现点击Home键的效果
                case 1:
                    Intent intent = new Intent(Intent.ACTION_MAIN);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识

                    intent.addCategory(Intent.CATEGORY_HOME);

                    startActivity(intent);
                    break;
            }


        }
    };

    public DetectorService2() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
