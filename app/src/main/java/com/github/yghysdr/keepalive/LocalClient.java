package com.github.yghysdr.keepalive;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;


/**
 * 本地客户端，消息接受发送
 */
public class LocalClient {
    private static String TAG = "LocalClient";

    private Messenger serviceMessenger;
    private Messenger clientMessenger = new Messenger(new ClientHandler());
    private Application mApplication;
    private SparseArray<Message> mWaitMessages = new SparseArray<>();

    public static LocalClient getInstance() {
        return SingleHolder.instance;
    }

    private final static class SingleHolder {
        static LocalClient instance = new LocalClient();
    }

    public void init(Application application) {
        mApplication = application;
        Intent intent = new Intent(application, RemoteServer.class);
        application.startService(intent);
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            serviceMessenger = new Messenger(service);
            Message message = mWaitMessages.get(IReceiveApi.CMD_1);
            if (message != null) {
                sendMessage(message);
            }
            mWaitMessages.remove(IReceiveApi.CMD_1);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            serviceMessenger = null;
            init(mApplication);
        }
    };

    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage: " + msg.what);
        }
    }


    public void sendMessage(Message message) {
        try {
            Log.d(TAG, "sendMessage: " + message.what);
            if (serviceMessenger != null) {
                serviceMessenger.send(message);
            } else {
                mWaitMessages.put(message.what, message);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送请求
     */
    public void sendMessage(int action, Bundle bundle) {
        Message msg = Message.obtain();
        msg.replyTo = clientMessenger;
        msg.what = action;
        if (bundle != null) {
            msg.setData(bundle);
        }
        sendMessage(msg);
    }

}
