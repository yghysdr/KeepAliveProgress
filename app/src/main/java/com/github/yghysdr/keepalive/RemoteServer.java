package com.github.yghysdr.keepalive;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


/**
 * 远程服务，消息接受发送
 */
public class RemoteServer extends Service {
    private static final String TAG = "RemoteServer";

    private HandlerThread workThread;//工作线程
    private Handler workHandler;//工作线程的Handler

    /**
     * 构建服务端信使
     */
    private Messenger serviceMessenger = new Messenger(new ServiceHandler());

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        workThread = new HandlerThread("Work Thread");
        workThread.start();
        workHandler = new Handler(workThread.getLooper(), callback);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        workThread.quitSafely();//当该服务销毁时，安全退出工作线程。
    }

    /**
     * 工作线程的回调消息处理，意味着一下这段代码运行在工作线程中
     */
    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final Message message = Message.obtain(msg);
            Log.e(TAG, "handleMessage: " + message.what);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        if (message.what == 1000) {
                            sendMsg(message, 1000 + message.what);
                            throw new RuntimeException("");
                        } else {
                            sendMsg(message, 1000 + message.what);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return false;
        }
    };


    public void sendMsg(Message msg, int intent) {
        sendMsg(msg, intent, null);
    }

    public void sendMsg(Message msg, int intent, Bundle bundle) {
        try {
            Message clientMsg = Message.obtain();
            clientMsg.what = intent;
            if (bundle != null) {
                clientMsg.setData(bundle);
            }
            msg.replyTo.send(clientMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务端Messenger信使的Handler回调处理
     */
    private class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //此处工作线程的Handler把重新封装的message发送到workThread工作线程中去（耗时任务放在线程中）
            Message message = Message.obtain(msg);
            workHandler.sendMessage(message);
        }
    }

}
