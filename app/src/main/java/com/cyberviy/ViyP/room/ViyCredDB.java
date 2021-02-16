package com.cyberviy.ViyP.room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.cyberviy.ViyP.models.ViyCred;

@Database(entities = {ViyCred.class}, version = 5)
public abstract class ViyCredDB extends RoomDatabase {

    private static ViyCredDB instance;

    public abstract ViyCredDao viyCredDao();

    public static synchronized ViyCredDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ViyCredDB.class, "CredsDB")
                    .setJournalMode(JournalMode.TRUNCATE)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private ViyCredDao viyCredDao;

        private PopulateDbAsyncTask(ViyCredDB db) {
            viyCredDao = db.viyCredDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Do anything
            return null;
        }
    }
}