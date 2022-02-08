package com.example.ContactList.Model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao
{
    @Query("SELECT * FROM Contact")
    List<Contact> getAllContacts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Contact... contacts);
}
