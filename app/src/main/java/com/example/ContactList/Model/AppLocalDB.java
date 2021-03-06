package com.example.ContactList.Model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ContactList.MyApplication;

@Database(entities = {Contact.class}, version = 3)
abstract class AppLocalDbRepository extends RoomDatabase
{
    public abstract ContactDao contactDao();
}

public class AppLocalDB
{
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getAppContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();
}
