package com.pd.chatapp.Database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.pd.chatapp.Dao.ContactsDao;
import com.pd.chatapp.model.user.Users;

@Database(entities = {Users.class}, version = 5)
public abstract class ContactsDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ChatAppDatabase";

    public abstract ContactsDao contactsDao();

    private static volatile ContactsDatabase INSTANCE;

    public static ContactsDatabase getInstance(Context context){
        if (INSTANCE == null){
            synchronized (ContactsDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context, ContactsDatabase.class,
                            DATABASE_NAME)
                            .addCallback(callback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateAsynTask(INSTANCE);
        }
    };
    static class PopulateAsynTask extends AsyncTask<Void,Void,Void> {

        private ContactsDao contactsDao;
        PopulateAsynTask(ContactsDatabase contactsDatabase){
            contactsDao = contactsDatabase.contactsDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            contactsDao.deleteAll();
            return null;
        }
    }
}
