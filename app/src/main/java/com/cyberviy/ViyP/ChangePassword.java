package com.cyberviy.ViyP;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ChangePassword extends AppCompatActivity {
    public static final String EXTRA_TYPE_PASS = "com.cyberviy.ViyP.EXTRA_TYPE_PASS";
    final String PREFS_NAME = "appEssentials";
    String TYPE_PASS_1 = "PIN";
    String TYPE_PASS_2 = "PASSWORD";
    String PREF = "HASH";
    MasterKey masterKey;
    //  String PREF_VAL;
    EditText old_password_et, new_password_1_et, new_password_2_et;
    Button submit;
    String old_password, new_password_1, new_password_2;
    String TYPE_PASSWORD;
    SharedPreferences sharedPreferences = null;
    String PREF_NAME = "Settings";
    String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        old_password_et = findViewById(R.id.old_password);
        new_password_1_et = findViewById(R.id.change_password_1);
        new_password_2_et = findViewById(R.id.change_password_2);
        submit = findViewById(R.id.submit);

        // Encrypted SharedPrefs
        try {
            //x.security
            masterKey = new MasterKey.Builder(getApplicationContext(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            //init sharedpPef
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

        if (sharedPreferences.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            try {
                ImageButton copyImage = findViewById(R.id.copy);
                copyImage.setEnabled(false);
            } catch (Exception e) {
                e.getStackTrace();
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        TYPE_PASSWORD = getIntent().getStringExtra(EXTRA_TYPE_PASS);

        preBuiltFormalities(TYPE_PASSWORD);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    savePassword(new_password_1);
                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void preBuiltFormalities(String TYPE) {
        if (TYPE.equals(TYPE_PASS_1)) {
            new_password_1_et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            new_password_2_et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

            new_password_1_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            new_password_2_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

        } else if (TYPE.equals(TYPE_PASS_2)) {
            new_password_1_et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            new_password_2_et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

            new_password_1_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
            new_password_2_et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        }
    }

    private boolean validate() {

        //Taking input to string
        old_password = old_password_et.getText().toString();
        new_password_1 = new_password_1_et.getText().toString();
        new_password_2 = new_password_2_et.getText().toString();

        //Fetching hash from sharedPref
        String PREF_VAL = sharedPreferences.getString(PREF, "0");
        Log.d(PREF, PREF_VAL);
        if (!PREF_VAL.equals(old_password)) {
            old_password_et.requestFocus();
            old_password_et.setError("Wrong Password");
            Log.d(PREF, "Previous: " + PREF_VAL + "Previous password: " + old_password);
            return false;
        }
        if (!(new_password_1.equals(new_password_2))) {
            new_password_2_et.requestFocus();
            new_password_2_et.setError("Password mismatch");
            return false;
        }
        return true;
    }

    private void savePassword(String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF, password).apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}