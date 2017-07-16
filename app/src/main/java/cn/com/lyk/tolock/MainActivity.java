package cn.com.lyk.tolock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import cn.com.lyk.base.IntentWrapper;
import cn.com.lyk.service.DetectorService;
import cn.com.lyk.service.TraceServiceImpl;

public class MainActivity extends Activity {
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
    // 数组长度代表点击次数
    long[] mHits = new long[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_main);
        checkVersion();
        Intent intent = new Intent(MainActivity.this, TraceServiceImpl.class);
        startService(intent);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvIndex:
                // 数组依次先前移动一位
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后运行时间
                if (mHits[0] >= (mHits[mHits.length - 1] - 500)) {
                    Toast.makeText(this, "3连击", Toast.LENGTH_LONG).show();
                    final EditText editText = new EditText(this);
                    //设置EditText文本为隐藏的
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    new AlertDialog.Builder(this)
                            .setTitle("请输入密码")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String key = editText.getText().toString().trim();
                                    if (isRight(key))
                                        IntentWrapper.whiteListMatters(MainActivity.this, "轨迹跟踪服务的持续运行");
                                    else
                                        Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    //检查如果我们运行在Android 5.0或更高版本
    private void checkVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
    }

    private static boolean isRight(String key) {
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR);
        int miniTenNum = minute / 10; // 分钟十位上的数字
        int miniFirstNum = minute % 10; // 分钟个位上的数字
        int hourFiestNum = hour % 10; // 小时个位上的数字
        // 验证，直接验证中间的时间对不对
        String rightKey = "" + hourFiestNum + miniTenNum + miniFirstNum;
        int rightKeyIndex = key.indexOf(rightKey, miniTenNum); // 密码关键字在用户输入中的位置

        if (key.contains(rightKey) && rightKeyIndex == miniTenNum) {
            return true;
        }
        return false;


    }

}
