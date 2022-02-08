package com.example.ContactList.Model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.ContactList.MyApplication;

import java.util.List;

public class Model
{
    public final static Model instance = new Model();
    ModelFireBase modelFireBase = new ModelFireBase();
    MutableLiveData<List<Contact>> ContactListLd = new MutableLiveData<List<Contact>>();

    private Model()
    {
        reloadContactList();
    }

    public interface getAllContactsListener
    {
        void onComplete(List<Contact> data);
    }

    public void reloadContactList()
    {
        //1.get local last update
        Long localLastUpdate = Contact.getLocalLastUpdated();
        //2.get all cars record since local last update from firebase
        modelFireBase.getAllContacts(localLastUpdate, new getAllContactsListener()
        {
            @Override
            public void onComplete(List<Contact> car_data)
            {
                //3.update local last update date
                //4.add new records to the local db
                MyApplication.executorService.execute(()->
                {
                    Long lLastUpdate = new Long(0);
                    Log.d("TAG", "FB returned " + car_data.size());
                    for(Contact c: car_data)
                    {

                        AppLocalDB.db.contactDao().insertAll(c);
                        if(c.getLastUpdated() > lLastUpdate)
                        {
                            lLastUpdate=c.getLastUpdated();
                        }
                    }
                    Contact.setLocalLastUpdated(lLastUpdate);
                    //5.return all records to the caller
                    List<Contact> coList = AppLocalDB.db.contactDao().getAllContacts();
                    ContactListLd.postValue(coList);
                });
            }
        });
    }

    public MutableLiveData<List<Contact>> getAll()
    {
        return ContactListLd;
    }

    public interface AddContactListener
    {
        void onComplete();
    }

    public void addContact(Contact contact, AddContactListener listener)
    {
        modelFireBase.addContact(contact, ()->
        {
            reloadContactList();
            listener.onComplete();
        });
    }
}
