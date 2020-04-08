package com.pratikthorat.coronatracker.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Collector.class}, version = 1, exportSchema = false)
public abstract class CollectorDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess();
}
