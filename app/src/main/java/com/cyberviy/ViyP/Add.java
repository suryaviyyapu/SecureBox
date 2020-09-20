package com.cyberviy.ViyP;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.cyberviy.ViyP.Utils.AESUtils;
import com.himanshurawat.hasher.HashType;
import com.himanshurawat.hasher.Hasher;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Add extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_PROVIDER_NAME = "com.cyberviy.ViyP.EXTRA_PROVIDER_NAME";
    public static final String EXTRA_PROVIDER = "com.cyberviy.ViyP.EXTRA_PROVIDER";
    public static final String EXTRA_ENCRYPT = "com.cyberviy.ViyP.EXTRA_ENCRYPT";
    public static final String EXTRA_EMAIL = "com.cyberviy.ViyP.EXTRA_EMAIL";
    public static final String EXTRA_IV = "com.cyberviy.ViyP.EXTRA_IV";
    public static final String EXTRA_SALT = "com.cyberviy.ViyP.EXTRA_SALT";
    public static final String PASSWORD = "";
    final String PREFS_NAME = "appEssentials";
    MasterKey masterKey = null;
    String providerNameString, passwordFromCOPY;
    String[] providersEmail = {
            "Gmail", "Outlook", "Amazon", "Protonmail", "Yahoo",
            "Apple", "Paypal", "Github", "Spotify", "Stackoverflow",
            "Trello", "Wordpress", "Other"
    };
    String[] providersSocial = {
            "Facebook", "Instagram", "Twitter", "Medium", "Flickr",
            "Foursquare", "Reddit", "Slack", "Snapchat", "Tinder",
            "Linkedin", "Pinterest", "Tumblr", "Other"
    };
    Button add_button;
    Spinner providerName;
    TextView prov_tv;
    String provider;
    SharedPreferences sharedPreferences = null;
    String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";
    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //ProgressBar progressBar = findViewById(R.id.progress_bar);
        providerName = findViewById(R.id.provider_name);
        email = findViewById(R.id.add_email);
        password = findViewById(R.id.add_password);
        CheckBox checkBox = findViewById(R.id.add_show_password);
        add_button = findViewById(R.id.add_record);
        prov_tv = findViewById(R.id.prov_tv);


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

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                } else {
                    password.setInputType(129);
                }
            }
        });

        add_button.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        provider = getIntent().getStringExtra(EXTRA_PROVIDER);
        if (provider == null)
            provider = "mail";
        passwordFromCOPY = getIntent().getStringExtra(PASSWORD);
        assert provider != null;
        switch (provider) {
            case "social":
                email.setHint("Username/Email");
                ArrayAdapter arrayAdapterSocial = new ArrayAdapter(this, android.R.layout.simple_spinner_item, providersSocial);
                arrayAdapterSocial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                providerName.setAdapter(arrayAdapterSocial);
                providerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        providerNameString = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case "wifi":
                prov_tv.setVisibility(View.GONE);
                providerName.setVisibility(View.GONE);
                email.setHint("SSID");
                break;
            default:
                email.setHint("Email");
                password.setText(passwordFromCOPY);
                ArrayAdapter arrayAdapterEmail = new ArrayAdapter(this, android.R.layout.simple_spinner_item, providersEmail);
                arrayAdapterEmail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                providerName.setAdapter(arrayAdapterEmail);
                providerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        providerNameString = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
        }

    }

    private void save_data() {
        String text_email, text_password;
        text_email = email.getText().toString();
        text_password = password.getText().toString();
        String sha = sharedPreferences.getString("HASH", "0");
        String HASH = Hasher.Companion.hash(sha, HashType.MD5);

        if (provider.equals("mail")) {
            if (text_email.trim().isEmpty()) {
                email.setError("Required");
                email.requestFocus();
                return;
            }
            String regex_email = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
            Pattern pattern = Pattern.compile(regex_email);
            Matcher matcher = pattern.matcher(text_email);
            //TODO Change if
            if (!matcher.matches()) {
                email.setError("Enter valid email");
                email.requestFocus();
                return;
            }
        }
        if (text_password.trim().isEmpty()) {
            password.setError("Required");
            password.requestFocus();
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PROVIDER_NAME, providerNameString);
        // AES UTILS ENC and DEC
        try {
            String encEmail = AESUtils.encrypt(text_email, HASH);
            String encPass = AESUtils.encrypt(text_password, HASH);
            intent.putExtra(EXTRA_EMAIL, encEmail);
            intent.putExtra(EXTRA_ENCRYPT, encPass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_record) {
            save_data();
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
}