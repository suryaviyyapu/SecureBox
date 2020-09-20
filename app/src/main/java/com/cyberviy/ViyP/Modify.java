package com.cyberviy.ViyP;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.cyberviy.ViyP.Utils.AESUtils;
import com.himanshurawat.hasher.HashType;
import com.himanshurawat.hasher.Hasher;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Modify extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MODIFY";
    public static final String EXTRA_DELETE = "DELETE";
    public static final String EXTRA_PROVIDER_NAME = "com.cyberviy.ViyP.EXTRA_PROVIDER_NAME";
    public static final String EXTRA_ID = "com.cyberviy.ViyP.EXTRA_ID";
    public static final String EXTRA_ENCRYPT = "com.cyberviy.ViyP.EXTRA_ENCRYPT";
    public static final String EXTRA_EMAIL = "com.cyberviy.ViyP.EXTRA_EMAIL";
    public static final String EXTRA_IV = "com.cyberviy.ViyP.EXTRA_IV";
    public static final String EXTRA_SALT = "com.cyberviy.ViyP.EXTRA_SALT";
    private static final String PREFS_NAME = "appEssentials";
    EditText newPassword;
    TextView emailText, oldPassword;
    String provName, email, passwd, decPass;
    CheckBox show_change_password, show_password;
    MasterKey masterKey = null;
    Button changePasswordButton, updateBtn, deleteBtn;
    SharedPreferences sharedPreferences = null;
    String PREF_NAME = "appEssentials";
    String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";
    LinearLayout newPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        emailText = findViewById(R.id.modify_email);
        oldPassword = findViewById(R.id.modify_old_password);
        show_password = findViewById(R.id.show_password);
        changePasswordButton = findViewById(R.id.change_password_button);
        newPassword = findViewById(R.id.modify_new_password);
        show_change_password = findViewById(R.id.modify_show_password);
        updateBtn = findViewById(R.id.modify_update);
        deleteBtn = findViewById(R.id.modify_delete);

        updateBtn.setEnabled(false);

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

        if (sharedPreferences.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        String sha = sharedPreferences.getString("HASH", "0");
        String HASH = Hasher.Companion.hash(sha, HashType.MD5);
        //DECRYPT
        Intent intent = getIntent();
        provName = intent.getStringExtra(EXTRA_PROVIDER_NAME);
        email = intent.getStringExtra(EXTRA_EMAIL);
        passwd = intent.getStringExtra(EXTRA_ENCRYPT);
        try {
            String decEmail = AESUtils.decrypt(email, HASH);
            decPass = AESUtils.decrypt(passwd, HASH);

            emailText.setText(decEmail);
            oldPassword.setText(decPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        show_change_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                } else {
                    newPassword.setInputType(129);
                }
            }
        });

        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    oldPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                } else {
                    oldPassword.setInputType(129);
                }
            }
        });
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
    }


    private void changePassword() {
        updateBtn.setEnabled(true);
        findViewById(R.id.show_password).setVisibility(View.GONE);
        changePasswordButton.setVisibility(View.GONE);
        newPasswordLayout = findViewById(R.id.change_password);
        newPasswordLayout.setVisibility(View.VISIBLE);
    }


    private void delete_data() {

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DELETE, true);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_ENCRYPT, passwd);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            intent.putExtra(EXTRA_ID, id);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void modify_data() {
        String text_old_password, text_new_password;
        text_old_password = oldPassword.getText().toString();
        text_new_password = newPassword.getText().toString();
        String sha = sharedPreferences.getString("HASH", "0");
        String HASH = Hasher.Companion.hash(sha, HashType.MD5);

        if (text_old_password.trim().isEmpty()) {
            oldPassword.setError("Required");
            oldPassword.requestFocus();
            return;
        }
        if (text_new_password.trim().isEmpty()) {
            newPassword.setError("Required");
            newPassword.requestFocus();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PROVIDER_NAME, provName);
        intent.putExtra(EXTRA_EMAIL, email);
        try {
            String encPass = AESUtils.encrypt(text_new_password, HASH);
            intent.putExtra(EXTRA_ENCRYPT, encPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            intent.putExtra(EXTRA_ID, id);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.modify_update && updateBtn.isEnabled()) {
            modify_data();
        } else if (v.getId() == R.id.modify_delete) {
            delete_data();
        } else if (v.getId() == R.id.change_password_button) {
            changePassword();
        }
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

    public void copy_email(View view) {
        if (sharedPreferences.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            Toast.makeText(this, "Secure code mode is Enabled. Copying is not allowed  ", Toast.LENGTH_SHORT).show();
        } else {
            TextView textView = findViewById(R.id.modify_email);
            final String gn_email = textView.getText().toString().trim();
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Email", gn_email);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Email Copied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void copy_password(View view) {
        if (sharedPreferences.getBoolean(PREF_KEY_SECURE_CORE_MODE, false)) {
            Toast.makeText(this, "Secure code mode is Enabled. Copying is not allowed  ", Toast.LENGTH_SHORT).show();
        } else {
            TextView textView = findViewById(R.id.modify_old_password);
            final String gn_password = textView.getText().toString().trim();
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", gn_password);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Password Copied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}