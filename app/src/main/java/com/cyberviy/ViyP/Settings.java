package com.cyberviy.ViyP;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.cyberviy.ViyP.ui.password.PasswordViewModel;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class Settings extends AppCompatActivity {
    final String PREFS_NAME = "appEssentials";
    SharedPreferences sharedPreferences = null;
    SharedPreferences UIPref;
    String PREF_KEY = "MASTER_PASSWORD";
    String PREF_DARK = "DARK_THEME";
    String PREF_KEY_SECURE_CORE_MODE = "SECURE_CORE";
    String PREF_KEY_SCM_COPY = "SCM_COPY";
    String PREF_KEY_SCM_SCREENSHOTS = "SCM_SCREENSHOTS";
    String NO_DATA = "NO DATA";
    String TYPE_PASS_1 = "PIN";
    String TYPE_PASS_2 = "PASSWORD";
    MasterKey masterKey = null;
    String PACKAGE_NAME;
    String repo = "suryaviyyapu";
    String pack = "ViyP";
    TextView change_password, export_data, delete_data, about_app;
    Button updateApp;
    ProgressBar progressBar;
    boolean secureCodeModeState;
    private int STORAGE_PERMISSION_CODE = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        change_password = findViewById(R.id.change_master_password);
        //export_data = findViewById(R.id.export_data);
        delete_data = findViewById(R.id.delete_all_data);
        about_app = findViewById(R.id.about_app);
        updateApp = findViewById(R.id.update_app);
        progressBar = findViewById(R.id.progress_bar);
        final SwitchMaterial askPasswordLaunchSwitch = findViewById(R.id.ask_password_launch);
        final SwitchMaterial secureCoreModeSwitch = findViewById(R.id.secure_core_mode);
        final SwitchMaterial dark_theme = findViewById(R.id.ask_dark_theme);

        secureCodeModeState = sharedPreferences.getBoolean(PREF_KEY_SECURE_CORE_MODE, false);
        final boolean askPasswordLaunchState = sharedPreferences.getBoolean(PREF_KEY, true);
        secureCoreModeSwitch.setChecked(secureCodeModeState);
        askPasswordLaunchSwitch.setChecked(askPasswordLaunchState);
        //Set theme mode
        UIPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean onDarkTheme = UIPref.getBoolean(PREF_DARK, false);
        if (onDarkTheme) {
            dark_theme.setChecked(onDarkTheme);
        }
        updateApp.setEnabled(true);

        //Checking for updates
        updateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!secureCodeModeState) {
                    progressBar.setVisibility(View.VISIBLE);
                    updateApp();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), "Secure code mode enabled cannot check for updates", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        askPasswordLaunchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // ask for password
                    askPassword(true);
                    editor.putBoolean(PREF_KEY, true).apply();
                } else {
                    // remove password
                    askPassword(false);
                    editor.putBoolean(PREF_KEY, false).apply();
                }
            }
        });
        final SharedPreferences.Editor UIEditor = UIPref.edit();
        dark_theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Enable Dark theme
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES
                    );
                    UIEditor.putBoolean(PREF_DARK, true).apply();
                } else {
                    // Disable Dark theme
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO
                    );
                    UIEditor.putBoolean(PREF_DARK, false).apply();
                }
            }
        });
        secureCoreModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Removing
                    secureCodeMode(true);
                    editor.putBoolean(PREF_KEY_SECURE_CORE_MODE, true).apply();
                } else {
                    secureCodeMode(false);
                    editor.putBoolean(PREF_KEY_SECURE_CORE_MODE, false).apply();
                }
            }
        });
    }

    private void secureCodeMode(boolean state) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (state) {
            //to do False
            //remove copy to clipboard and screenshot ability

            editor.putBoolean(PREF_KEY_SCM_COPY, false).apply();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            Toast.makeText(getApplicationContext(), "Secure code mode is active. Restart to apply changes", Toast.LENGTH_LONG).show();
        } else {
            //to do true
            //set copy to clipboard and screenshot ability
            editor.putBoolean(PREF_KEY_SCM_SCREENSHOTS, true).apply();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            Toast.makeText(getApplicationContext(), "Secure code mode is inactive", Toast.LENGTH_LONG).show();
        }
    }

    public void updateApp() {
        AppUpdater appUpdater = new AppUpdater(this)
                .showEvery(5)
                .setDisplay(Display.NOTIFICATION)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo(repo, pack);
        appUpdater.start();
    }

    private void askPassword(boolean state) {
        if (state) {
            //to do False
            //remove copy to clipboard and screenshot ability
            Toast.makeText(getApplicationContext(), "Password: ON", Toast.LENGTH_LONG).show();
        } else {
            //to do true
            //set copy to clipboard and screenshot ability
            Toast.makeText(getApplicationContext(), "Password: OFF", Toast.LENGTH_LONG).show();
        }
    }

    public void changePassword(View view) {
        TextView PIN = findViewById(R.id.change_master_password_option_1);
        //TODO Change to password disabled for now
        //TextView Password = findViewById(R.id.change_master_password_option_2);
        PIN.setVisibility(View.VISIBLE);
        //Password.setVisibility(View.VISIBLE);
    }

    public void changePasswordToPIN(View view) {
        Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
        intent.putExtra(ChangePassword.EXTRA_TYPE_PASS, TYPE_PASS_1);
        startActivity(intent);
    }

    public void changePasswordToPassword(View view) {
        Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
        intent.putExtra(ChangePassword.EXTRA_TYPE_PASS, TYPE_PASS_2);
        startActivity(intent);
    }

    public void getItemsForExport() {

    }

    public void exportData(View view) {
//        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        Toast.makeText(getApplicationContext(), "Export data", Toast.LENGTH_SHORT).show();
    }

    public void restoreData(View view) throws IOException {
        // Restore
        Toast.makeText(getApplicationContext(), "Restore data successful", Toast.LENGTH_SHORT).show();
    }


    public void deleteData(View view) {
        //AlertDialog START
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle("Delete Everything");
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("Are you sure, You want to delete everything?");
        alertDialogBuilder.setCancelable(false);
        //Positive button
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                PasswordViewModel passwordViewModel = new PasswordViewModel(getApplication());
                progressBar.setVisibility(View.VISIBLE);
                passwordViewModel.deleteAllNotes();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NO_DATA, false).apply();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        //Negative button
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        //AlertDialog END

    }

    public void aboutApp(View view) {
        startActivity(new Intent(this, About.class));
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