package com.cyberviy.ViyP.repos;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.cyberviy.ViyP.models.ViyCred;
import com.cyberviy.ViyP.room.ViyCredDB;
import com.cyberviy.ViyP.room.ViyCredDao;

import java.util.List;

public class CredsRepository {
    private ViyCredDao viyCredDao;
    private LiveData<List<ViyCred>> allCreds, mailCreds, socialCreds, wifiCreds;

    public CredsRepository(Application application) {
        ViyCredDB database = ViyCredDB.getInstance(application);
        viyCredDao = database.viyCredDao();
        allCreds = viyCredDao.getAllCreds();
        mailCreds = viyCredDao.getAllMails();
        socialCreds = viyCredDao.getAllSocial();
        wifiCreds = viyCredDao.getAllWifi();
    }

    public void insert(ViyCred viyCred) {
        new InsertNoteAsyncTask(viyCredDao).execute(viyCred);
    }

    public void update(ViyCred viyCred) {
        new UpdateNoteAsyncTask(viyCredDao).execute(viyCred);
    }

    public void delete(ViyCred viyCred) {
        new DeleteNoteAsyncTask(viyCredDao).execute(viyCred);
    }

    public void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(viyCredDao).execute();
    }

    public LiveData<List<ViyCred>> getAllNotes() {
        return allCreds;
    }

    public LiveData<List<ViyCred>> getAllMails() {
        return mailCreds;
    }

    public LiveData<List<ViyCred>> getAllWifi() {
        return wifiCreds;
    }

    public LiveData<List<ViyCred>> getAllSocial() {
        return socialCreds;
    }

    private static class InsertNoteAsyncTask extends AsyncTask<ViyCred, Void, Void> {
        private ViyCredDao viyCredDao;

        private InsertNoteAsyncTask(ViyCredDao viyCredDao) {
            this.viyCredDao = viyCredDao;
        }

        @Override
        protected Void doInBackground(ViyCred... viyCreds) {
            viyCredDao.insert(viyCreds[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<ViyCred, Void, Void> {
        private ViyCredDao viyCredDao;

        private UpdateNoteAsyncTask(ViyCredDao viyCredDao) {
            this.viyCredDao = viyCredDao;
        }

        @Override
        protected Void doInBackground(ViyCred... viyCreds) {
            viyCredDao.update(viyCreds[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<ViyCred, Void, Void> {
        private ViyCredDao viyCredDao;

        private DeleteNoteAsyncTask(ViyCredDao viyCredDao) {
            this.viyCredDao = viyCredDao;
        }

        @Override
        protected Void doInBackground(ViyCred... viyCreds) {
            viyCredDao.delete(viyCreds[0]);
            return null;
        }
    }

     private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
         private ViyCredDao viyCredDao;

         private DeleteAllNotesAsyncTask(ViyCredDao viyCredDao) {
             this.viyCredDao = viyCredDao;
         }

         @Override
         protected Void doInBackground(Void... voids) {
             viyCredDao.deleteAllNotes();
             return null;
        }
    }
}
