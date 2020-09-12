package com.cyberviy.ViyP;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.himanshurawat.hasher.HashType;
import com.himanshurawat.hasher.Hasher;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Modify extends Activity implements View.OnClickListener {
    EditText newPassword;
    TextView emailText, oldPassword, showPassword;
    String provName, email, passwd, decPass;
    CheckBox checkBox;
    MasterKey masterKey = null;
    Button changePasswordButton, updateBtn, deleteBtn;
    SharedPreferences sharedPreferences = null;
    String PREF_NAME = "appEssentials";
    String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";
    LinearLayout newPasswordLayout;
    private static final String PREFS_NAME = "appEssentials";
    public static final String TAG = "MODIFY";
    public static final String EXTRA_DELETE = "DELETE";
    public static final String EXTRA_PROVIDER_NAME = "com.cyberviy.ViyP.EXTRA_PROVIDER_NAME";
    public static final String EXTRA_ID = "com.cyberviy.ViyP.EXTRA_ID";
    public static final String EXTRA_ENCRYPT = "com.cyberviy.ViyP.EXTRA_ENCRYPT";
    public static final String EXTRA_EMAIL = "com.cyberviy.ViyP.EXTRA_EMAIL";
    public static final String EXTRA_IV = "com.cyberviy.ViyP.EXTRA_IV";
    public static final String EXTRA_SALT = "com.cyberviy.ViyP.EXTRA_SALT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        emailText = findViewById(R.id.modify_email);
        oldPassword = findViewById(R.id.modify_old_password);
        showPassword = findViewById(R.id.show_password);
        changePasswordButton = findViewById(R.id.change_password_button);
        newPassword = findViewById(R.id.modify_new_password);
        checkBox = findViewById(R.id.modify_show_password);
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
            try {
                ImageButton copyImage = findViewById(R.id.copy);
                copyImage.setEnabled(false);
            } catch (Exception e) {
                e.getStackTrace();
            }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newPassword.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                } else {
                    newPassword.setInputType(129);
                }
            }
        });
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        showPassword.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
    }


    private void changePassword() {
        updateBtn.setEnabled(true);
        changePasswordButton.setVisibility(View.GONE);
        newPasswordLayout = findViewById(R.id.change_password);
        newPasswordLayout.setVisibility(View.VISIBLE);
    }

    private void showPassword() {
        oldPassword.setText(decPass);
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
        } else if (v.getId() == R.id.show_password) {
            showPassword();
        } else if (v.getId() == R.id.change_password_button) {
            changePassword();
        }
    }
}