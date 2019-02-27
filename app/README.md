## 多进程

### 进程列表
- com.github.yghysdr.keepalive
  - LocalClient
- com.github.yghysdr.keepalive:server
  - RemoteServer

### com.github.yghysdr.keepalive:server 调起方式
```
public class LocalClient {
    public void init(Application application) {
        Intent intent = new Intent(application, RemoteServer.class);
        application.startService(intent);
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            serviceMessenger = null;
        }
    };
}
```

### RemoteServer终止的情况
#### RemoteServer本身异常时奔溃的时：
- RemoteServer所在进程被回收
- RemoteServer没触发onDestroy
- 绑定了server服务的LocalClient中的serviceConnection会收到onServiceDisconnected

#### 终止com.github.yghysdr.keepalive:server进程
- RemoteServer所在进程被回收
- RemoteServer没触发onDestroy
- 绑定了server服务的LocalClient中的serviceConnection会收到onServiceDisconnected

### 总结
- RemoteServer与com.github.yghysdr.keepalive:server是共存亡
- 无论RemoteServer被意外销毁或者回收后时，还是，会触发绑定者的onServiceDisconnected
- RemoteServer在一定情况下销毁后会重新建立连接

### 提高App生命周期内的存活
- 在客户端触发onServiceDisconnected时，重新初始化远程服务
- 在某些情况下（例如前后台切换），检查RemoteServer所在进程的存活情况，如果未存在则重新初始化远程服务