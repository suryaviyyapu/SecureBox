package com.cyberviy.ViyP;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    final String PREFS_NAME = "appEssentials";
    String PREF_KEY = "MASTER_PASSWORD";
    String PREF_KEY_FRUN = "FIRST RUN";
    MasterKey masterKey = null;
    SharedPreferences sharedPreferences = null;


    // Gradient on statusbar
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, R.color.bg_color_splash));
            //window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            //window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Encrypted SharedPrefs
                try {
                    //x.security
                    masterKey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                            .build();
                    //init sharedPef
                    sharedPreferences = EncryptedSharedPreferences.create(
                            getApplicationContext(),
                            PREFS_NAME,
                            masterKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
                final boolean askPasswordLaunchState = sharedPreferences.getBoolean(PREF_KEY, true);
                final boolean firstRun = sharedPreferences.getBoolean(PREF_KEY_FRUN, true);
                Log.d("ASK PASSWORD", String.valueOf(askPasswordLaunchState));
                if (firstRun) {
                    startActivity(new Intent(SplashActivity.this, Welcome.class));
                } else {
                    if (askPasswordLaunchState) {
                        startActivity(new Intent(SplashActivity.this, MLock.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, Home.class));
                        Toast.makeText(getApplicationContext(), "Consider using password", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        }, SPLASH_TIME_OUT);


    }
}