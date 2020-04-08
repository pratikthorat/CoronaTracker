package com.pratikthorat.coronatracker.Database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;


public class CollectorRepository {
    private String DB_NAME = "db_record";

    private CollectorDatabase collectorDatabase;

    public CollectorRepository(Context context) {
        collectorDatabase = Room.databaseBuilder(context, CollectorDatabase.class, DB_NAME).build();
    }

    public void insertRecord(String title,
                             String message,
                             String time) {

        Collector record = new Collector();
        record.setTitle(title);
        record.setMessage(message);
        record.setTime(time);

        insertRecord(record);
    }

    public void insertRecord(final Collector record) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                collectorDatabase.daoAccess().insertRecord(record);
                return null;
            }
        }.execute();
    }

    public void deleteAll() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                collectorDatabase.daoAccess().deleteAll();
                return null;
            }
        }.execute();
    }

    public List<Collector> getRecord(int id) {
        return collectorDatabase.daoAccess().getRecord(id);//, "2018-10-20"
    }

    public List<Collector> getAllRecords() {
        return collectorDatabase.daoAccess().fetchAllRecords();
    }
}
