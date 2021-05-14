package com.pratikthorat.coronatracker.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
