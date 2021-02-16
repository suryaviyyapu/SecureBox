package com.cyberviy.ViyP;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    final String PREFS_NAME = "appEssentials";
    String PREF_KEY = "MASTER_PASSWORD";
    String PREF_DARK = "DARK_THEME";
    String PREF_KEY_FRUN = "FIRST RUN";
    MasterKey masterKey = null;
    SharedPreferences sharedPreferences = null;


    // Gradient on statusbar
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.bg_color_splash));
        //window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        //window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences UIPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean UI = UIPref.getBoolean(PREF_DARK, false);
        if (UIPref.getBoolean(PREF_DARK, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_splash);
        TextView password_manager = findViewById(R.id.password_manager);
        password_manager.setText("Password Manager");
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
                if (firstRun) {
                    startActivity(new Intent(Splash.this, Welcome.class));
                } else {
                    if (askPasswordLaunchState) {
                        startActivity(new Intent(Splash.this, MLock.class));
                    } else {
                        startActivity(new Intent(Splash.this, Home.class));
                        Toast.makeText(getApplicationContext(), "Consider using password", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}