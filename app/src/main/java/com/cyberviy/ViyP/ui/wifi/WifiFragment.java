package com.cyberviy.ViyP.ui.wifi;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyberviy.ViyP.Add;
import com.cyberviy.ViyP.Modify;
import com.cyberviy.ViyP.R;
import com.cyberviy.ViyP.adapters.RecyclerViewAdapter;
import com.cyberviy.ViyP.models.ViyCred;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.app.Activity.RESULT_OK;

// Mails
public class WifiFragment extends Fragment {
    private static final String NO_DATA = "NO DATA";
    boolean status = false;
    private static Application application;
    private WifiViewModel wifiViewModel;
    private static final int ADD_RECORD = 1;
    private static final int MODIFY_RECORD = 2;
    private static final int DELETE_RECORD = 3;
    public static final String PROVIDER = "wifi";
    public static final String TAG = "W_FRAG";
    private TextView empty;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.wifi_fragment, container, false);


        //ProgressBar progressBar = root.findViewById(R.id.progress_bar);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        empty = root.findViewById(R.id.empty);
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(PROVIDER, Context.MODE_PRIVATE);
        status = sharedPreferences.getBoolean(NO_DATA, false);
        if (status) {
            empty.setVisibility(View.GONE);
        } else {
            empty.setText(NO_DATA);
        }
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        final RecyclerViewAdapter viewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(viewAdapter);


        wifiViewModel = new ViewModelProvider(this).get(WifiViewModel.class);
        wifiViewModel.getAllWifi().observe(getViewLifecycleOwner(), new Observer<List<ViyCred>>() {
            @Override
            public void onChanged(List<ViyCred> viyCreds) {
                viewAdapter.setCreds(viyCreds);
            }
        });
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                wifiViewModel.delete(viewAdapter.getCredAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Entry deleted", Toast.LENGTH_SHORT).show();
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        viewAdapter.setOnItemClickListener(new RecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(ViyCred viyCred) {
                Log.d(TAG, "Onclick");
                Intent intent = new Intent(getActivity(), Modify.class);
                intent.putExtra(Modify.EXTRA_ID, viyCred.getId());
                intent.putExtra(Modify.EXTRA_PROVIDER_NAME, viyCred.getProviderName());
                intent.putExtra(Modify.EXTRA_EMAIL, viyCred.getEmail());
                intent.putExtra(Modify.EXTRA_ENCRYPT, viyCred.getCat());
                startActivityForResult(intent, MODIFY_RECORD);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Add.class);
                intent.putExtra(Add.EXTRA_PROVIDER, PROVIDER);
                startActivityForResult(intent, ADD_RECORD);
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_RECORD && resultCode == RESULT_OK) {
            String enc_passwd = data.getStringExtra(Add.EXTRA_ENCRYPT);
            String enc_email = data.getStringExtra(Add.EXTRA_EMAIL);

            ViyCred viyCred = new ViyCred(PROVIDER, PROVIDER, enc_email, enc_passwd);
            Log.d(TAG, "Provider: " + PROVIDER + " EMAIL: " + enc_email + " ENC_DATA: " + enc_passwd);
            //For showing "No data" or not on activity if the list is empty
            SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(PROVIDER, Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean(NO_DATA, true).apply();
            empty.setVisibility(View.GONE);
            wifiViewModel.insert(viyCred);
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == MODIFY_RECORD && resultCode == RESULT_OK) {
            int id = data.getIntExtra(Modify.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getContext(), "Cannot be updated!", Toast.LENGTH_LONG).show();
                return;
            }
            String enc_passwd = data.getStringExtra(Modify.EXTRA_ENCRYPT);
            String enc_email = data.getStringExtra(Modify.EXTRA_EMAIL);
            ViyCred viyCred = new ViyCred(PROVIDER, PROVIDER, enc_email, enc_passwd);
            //IMP
            viyCred.setId(id);
            if (!data.getBooleanExtra(Modify.EXTRA_DELETE, false)) {
                Log.d(TAG, "Provider: " + PROVIDER + " EMAIL: " + enc_email + " ENC_DATA: " + enc_passwd);
                wifiViewModel.update(viyCred);
                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
            } else {
                wifiViewModel.delete(viyCred);
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
//
        } else if (requestCode == DELETE_RECORD && resultCode == RESULT_OK) {
//            int id = data.getIntExtra(Modify.EXTRA_ID, -1);
//            if (id == -1) {
//                Toast.makeText(getContext(), "Cannot be deleted!", Toast.LENGTH_LONG).show();
//                return;
//            }
//            String enc_passwd = data.getStringExtra(Modify.EXTRA_ENCRYPT);
//            String enc_email = data.getStringExtra(Modify.EXTRA_EMAIL);
//            ViyCred viyCred = new ViyCred(PROVIDER, enc_email, enc_passwd);
//            viyCred.setId(id);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
