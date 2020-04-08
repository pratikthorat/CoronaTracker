package com.pratikthorat.coronatracker.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DaoAccess {
    @Insert
    Long insertRecord(Collector collect);


    @Query("SELECT * FROM Collector ORDER BY messageId asc")
    List<Collector> fetchAllRecords();


    @Query("SELECT * FROM Collector WHERE messageId =:messageId")
    List<Collector> getRecord(int messageId);

    @Delete
    void deleteRecord(Collector collect);

    @Query("DELETE FROM Collector")
    void deleteAll();
}
