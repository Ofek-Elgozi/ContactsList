package com.example.ContactList.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.LinkedList;

public class ModelFireBase
{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void getAllContacts(Long since, Model.getAllContactsListener listener)
    {
        db.collection("contacts").whereGreaterThanOrEqualTo(Contact.LAST_UPDATED, new Timestamp(since, 0)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                LinkedList<Contact> contactList = new LinkedList<Contact>();
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot doc: task.getResult())
                    {
                        Contact c = Contact.fromJson(doc.getData());
                        c.setPhone(doc.getId());
                        if(c!=null)
                            contactList.add(c);
                    }
                }
                listener.onComplete(contactList);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                listener.onComplete(null);
            }
        });
    }

    public void addContact(Contact contact, Model.AddContactListener listener)
    {
        if(contact.getPhone()==null)
        {
            db.collection("contacts")
                    .document().set(contact.toJson())
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(@NonNull Void unused)
                        {
                            listener.onComplete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d("TAG", e.getMessage());
                        }
                    });
        }
        else
        {
            db.collection("contacts")
                    .document(contact.getPhone()).set(contact.toJson())
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(@NonNull Void unused)
                        {
                            listener.onComplete();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d("TAG", e.getMessage());
                        }
                    });
        }
    }
}
