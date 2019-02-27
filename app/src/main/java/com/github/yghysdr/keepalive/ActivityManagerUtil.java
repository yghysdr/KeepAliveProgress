package com.github.yghysdr.keepalive;

import android.app.ActivityManager;
import android.content.Context;

import java.io.IOException;
import java.util.List;

/**
 * Created by yghysdr on 2019/2/27.
 */
public class ActivityManagerUtil {

    public static boolean isProgressRunning(Context context, String progressName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(progressName)) {
                isRunning = true;
            }
        }
        return isRunning;
    }


    public static void killProgress(Context context, String progressName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : lists) {
            if (info.processName.equals(progressName)) {
                int pid = info.pid;
                killPid(pid);
                break;
            }
        }
    }

    public static void killPid(int pid) {
        String command = "kill -9 " + pid;
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
