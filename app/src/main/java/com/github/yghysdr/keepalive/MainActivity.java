package com.github.yghysdr.keepalive;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    String progressName = "com.github.yghysdr.keepalive:server";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalClient.getInstance().init(getApplication());
            }
        });

        findViewById(R.id.send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalClient.getInstance().sendMessage(1, null);
            }
        });

        findViewById(R.id.send_msg_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalClient.getInstance().sendMessage(1000, null);
            }
        });

        findViewById(R.id.check_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean proessRunning = ActivityManagerUtil.isProgressRunning(getApplication(), progressName);
                Log.d(TAG, "onClick: " + proessRunning);
            }
        });

        findViewById(R.id.kill_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManagerUtil.killProgress(getApplication(), progressName);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
