package com.wayto.demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.Start_Track_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(getAppVersionName(MainActivity.this, "com.wayto.track"))) {
                    Toast.makeText(MainActivity.this, "Please Install Track App", Toast.LENGTH_SHORT).show();
                }

                Bundle bundle=new Bundle();

                boolean success = openApp(MainActivity.this, bundle, "com.wayto.track", "com.wayto.track.TrackActivity");

                if (!success) {
                    Toast.makeText(MainActivity.this, "Open Track App Failure", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    String getAppVersionName(Context context, String packageName) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception" + e);
        }
        return versionName;
    }

    boolean openApp(Context context, Bundle build, String packageName, String className) {
        boolean isSuccess = true;
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            intent.putExtras(build);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }
}
