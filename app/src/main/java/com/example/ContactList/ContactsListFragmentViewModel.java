package com.example.ContactList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ContactList.Model.Contact;
import com.example.ContactList.Model.Model;

import java.util.List;

public class ContactsListFragmentViewModel extends ViewModel
{
    MutableLiveData<List<Contact>> data = Model.instance.getAll();

    public MutableLiveData<List<Contact>> getData()
    {
        return data;
    }
}
