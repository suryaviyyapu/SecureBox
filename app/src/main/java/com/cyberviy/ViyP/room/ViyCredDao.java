package com.cyberviy.ViyP.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cyberviy.ViyP.models.ViyCred;

import java.util.List;

@Dao
public interface ViyCredDao {

    @Insert
    void insert(ViyCred viyCred);

    @Update
    void update(ViyCred viyCred);

    @Delete
    void delete(ViyCred viyCred);

    @Query("DELETE FROM creds_table")
    void deleteAllNotes();

    @Query("SELECT * FROM creds_table")
    LiveData<List<ViyCred>> getAllCreds();

    @Query("SELECT * FROM creds_table WHERE provider = 'mail'")
    LiveData<List<ViyCred>> getAllMails();

    @Query("SELECT * FROM creds_table WHERE provider = 'wifi'")
    LiveData<List<ViyCred>> getAllWifi();

    @Query("SELECT * FROM creds_table WHERE provider = 'social'")
    LiveData<List<ViyCred>> getAllSocial();
}