package com.example.ContactList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.ContactList.Model.Model;

public class MainActivity extends AppCompatActivity
{
    NavController navCtrl;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavHostFragment nav_host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_nav_host);
        navCtrl = nav_host.getNavController();
        NavigationUI.setupActionBarWithNavController(this,navCtrl);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                navCtrl.navigateUp();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}