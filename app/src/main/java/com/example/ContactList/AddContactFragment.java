package com.example.ContactList;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.ContactList.Model.Contact;
import com.example.ContactList.Model.Model;

public class AddContactFragment extends Fragment
{
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragment_add_contact, container, false);
        EditText phoneEt = view.findViewById(R.id.addcontact_phone_et);
        EditText nameEt = view.findViewById(R.id.addcontact_name_et);
        EditText genderEt = view.findViewById(R.id.addcontact_gender_et);
        Button saveBtn= view.findViewById(R.id.addcontact_save_btn);
        Button cancelBtn= view.findViewById(R.id.addcontact_cancel_btn);
        progressBar = view.findViewById(R.id.addcontact_progressBar);
        progressBar.setVisibility(View.GONE);
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(View.VISIBLE);
                Contact contact = new Contact();
                contact.setName(nameEt.getText().toString());
                contact.setPhone(phoneEt.getText().toString());
                contact.setGender(genderEt.getText().toString());
                nameEt.setEnabled(false);
                phoneEt.setEnabled(false);
                genderEt.setEnabled(false);
                cancelBtn.setEnabled(false);
                Model.instance.addContact(contact,()->
                {
                    Navigation.findNavController(v).popBackStack();
                });
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Navigation.findNavController(v).popBackStack();
            }
        });
        return view;
    }
}