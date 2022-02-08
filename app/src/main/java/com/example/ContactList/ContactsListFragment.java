package com.example.ContactList;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ContactList.Model.Contact;
import com.example.ContactList.Model.Model;

import java.util.ArrayList;
import java.util.List;

public class ContactsListFragment extends Fragment
{
    ContactsListFragmentViewModel viewModel;
    View view;
    MyAdapter adapter;
    Contact c2;
    SwipeRefreshLayout swipeRefresh;
    ProgressBar progressBar;
    private static final int REQUEST_CALL = 1;

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(ContactsListFragmentViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        progressBar = view.findViewById(R.id.contacts_list_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView list = view.findViewById(R.id.mainlistfragment_listv);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager LayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(LayoutManager);
        DividerItemDecoration DividerList = new DividerItemDecoration(list.getContext(), LayoutManager.getOrientation());
        DividerList.setDrawable(getResources().getDrawable(R.drawable.divider));
        list.addItemDecoration(DividerList);
        adapter = new MyAdapter();
        list.setAdapter(adapter);
        swipeRefresh = view.findViewById(R.id.contactslist_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh() {
                Model.instance.reloadContactList();
                swipeRefresh.setRefreshing(false);
            }
        });

        if (viewModel.getData().getValue() == null) {
            Model.instance.reloadContactList();
            swipeRefresh.setRefreshing(false);
        }

        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Contact>>()
        {
            @Override
            public void onChanged(List<Contact> contacts)
            {
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            sendMessage(c2);
        } else {
            Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_CALL)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(c2);
            } else {
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameTv;
        TextView phoneTv;
        ImageButton sms_btn;
        ImageButton call_btn;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener)
        {
            super(itemView);
            sms_btn = itemView.findViewById(R.id.mainlistrow_sms);
            call_btn = itemView.findViewById(R.id.mainlistrow_call);
            nameTv = itemView.findViewById(R.id.mainlistrow_name);
            phoneTv = itemView.findViewById(R.id.mainlistrow_phone);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = getAdapterPosition();
                    if (listener != null)
                    {
                        listener.onItemClick(pos, v);
                    }
                }
            });
        }

        public void bind(Contact contact)
        {
            nameTv.setText(contact.getName());
            phoneTv.setText(contact.getPhone());
            sms_btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        sendMessage(contact);
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, 100);
                    }
                }
            });
            call_btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    makePhoneCall(contact);
                }
            });
        }
    }

    private void makePhoneCall(Contact c)
    {
        String number = c.getPhone();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    public void sendMessage(Contact c)
    {
        String sPhone = c.getPhone();
        String sName = c.getName();
        String sMessage = "Default Message";
        if (c.getGender().equals("Male")) {
            sMessage = "שלום " + sName + "! זמן רב שלא דיברנו. אנא התקשר אליי ברגע שאתה רואה את ההודעה.";
        } else if (c.getGender().equals("Female")) {
            sMessage = "שלום " + sName + "! זמן רב שלא דיברנו. אנא התקשרי אליי ברגע שאת רואה את ההודעה.";
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sPhone, null, sMessage, null, null);
        Toast.makeText(getActivity(), "SMS sent successfully", Toast.LENGTH_SHORT).show();
    }

    interface OnItemClickListener {
        void onItemClick(int position, View v);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>
    {
        private List<Contact> exampleListFull;

        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener)
        {
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View v = getLayoutInflater().inflate(R.layout.main_list_row, parent, false);
            MyViewHolder holder = new MyViewHolder(v, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
        {
            c2 = viewModel.getData().getValue().get(position);
            holder.bind(c2);
        }

        @Override
        public int getItemCount()
        {
            if (viewModel.getData().getValue() == null)
            {
                return 0;
            }
            return viewModel.getData().getValue().size();
        }

        public Filter getFilter()
        {
            return exampleFilter;
        }

        private Filter exampleFilter = new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                List<Contact> filteredList = new ArrayList<>();
                exampleListFull = new ArrayList<>(viewModel.getData().getValue());
                if (constraint == null || constraint.length() == 0)
                {
                    filteredList.addAll(exampleListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Contact item : exampleListFull) {
                        if (item.getPhone().toLowerCase().contains(filterPattern))
                        {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                viewModel.getData().getValue().clear();
                viewModel.getData().getValue().addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contacts_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_add:
                Navigation.findNavController(view).navigate(R.id.addContactFragment);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}


