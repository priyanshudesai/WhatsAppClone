package com.pd.chatapp.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pd.chatapp.model.user.Users;

import java.util.List;

@Dao
public interface ContactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Users> contacts);

    @Query("SELECT * FROM contacts")
    LiveData<List<Users>> getAllContacts();

    @Query("DELETE FROM contacts")
    void deleteAll();
}
