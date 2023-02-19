package com.pd.chatapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pd.chatapp.Repository.ContactsRepository;
import com.pd.chatapp.model.user.Users;

import java.util.List;

public class ContactsViewModel extends AndroidViewModel {

    private ContactsRepository contactsRepository;
    private LiveData<List<Users>> getAllContacts;

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        contactsRepository = new ContactsRepository(application);
        getAllContacts = contactsRepository.getAllContact();
    }

    public void insert(List<Users> list){
        contactsRepository.insert(list);
    }

    public void deleteAll() {contactsRepository.deleteAll();}

    public LiveData<List<Users>> getAllContact(){
        return getAllContacts;
    }
}
