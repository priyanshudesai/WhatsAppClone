package com.pd.chatapp.Repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.pd.chatapp.Dao.ContactsDao;
import com.pd.chatapp.Database.ContactsDatabase;
import com.pd.chatapp.model.user.Users;

import java.util.List;

public class ContactsRepository {
    private ContactsDatabase database;
    private LiveData<List<Users>> getAllContacts;
    public ContactsRepository(Application application){
        database = ContactsDatabase.getInstance(application);
        getAllContacts = database.contactsDao().getAllContacts();
    }

    public void insert(List<Users> contactList){
        new InsertAsyncTask(database).execute(contactList);
    }

    public LiveData<List<Users>> getAllContact(){
        return getAllContacts;
    }

    static class InsertAsyncTask extends AsyncTask<List<Users>, Void, Void> {
        private ContactsDao contactsDao;
        InsertAsyncTask(ContactsDatabase contactsDatabase){
            contactsDao = contactsDatabase.contactsDao();
        }
        @Override
        protected Void doInBackground(List<Users>... lists) {
            contactsDao.insert(lists[0]);
            return null;
        }
    }

    public void deleteAll()  {
        new deleteAllContactsAsyncTask(database).execute();
    }

    private static class deleteAllContactsAsyncTask extends AsyncTask<Void, Void, Void> {
        private ContactsDao contactsDao;

        deleteAllContactsAsyncTask(ContactsDatabase contactsDatabase) {
            contactsDao = contactsDatabase.contactsDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            contactsDao.deleteAll();
            return null;
        }
    }
}
