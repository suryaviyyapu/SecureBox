package com.cyberviy.ViyP.ui.wifi;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cyberviy.ViyP.models.ViyCred;
import com.cyberviy.ViyP.repos.CredsRepository;

import java.util.List;

public class WifiViewModel extends AndroidViewModel {
    private CredsRepository repository;
    private LiveData<List<ViyCred>> allCreds, wifiCreds;

    public WifiViewModel(@NonNull Application application) {
        super(application);
        repository = new CredsRepository(application);
        allCreds = repository.getAllNotes();
        wifiCreds = repository.getAllWifi();

    }

    public void insert(ViyCred viyCred) {
        repository.insert(viyCred);
    }

    public void update(ViyCred viyCred) {
        repository.update(viyCred);
    }

    public void delete(ViyCred viyCred) {
        repository.delete(viyCred);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<ViyCred>> getAllCreds() {
        return allCreds;
    }

    public LiveData<List<ViyCred>> getAllWifi() {
        return wifiCreds;
    }
}