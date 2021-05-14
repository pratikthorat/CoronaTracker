package com.pratikthorat.coronatracker.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Collector.class}, version = 1, exportSchema = false)
public abstract class CollectorDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess();
}
