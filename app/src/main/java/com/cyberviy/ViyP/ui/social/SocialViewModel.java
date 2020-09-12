package com.cyberviy.ViyP.ui.social;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cyberviy.ViyP.models.ViyCred;
import com.cyberviy.ViyP.repos.CredsRepository;

import java.util.List;

public class SocialViewModel extends AndroidViewModel {
    private CredsRepository repository;
    private LiveData<List<ViyCred>> allCreds, mailCreds;

    public SocialViewModel(@NonNull Application application) {
        super(application);
        repository = new CredsRepository(application);
        mailCreds = repository.getAllSocial();

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

    public LiveData<List<ViyCred>> getAllSocial() {
        return mailCreds;
    }
}