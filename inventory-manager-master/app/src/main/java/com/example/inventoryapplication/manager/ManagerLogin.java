package com.example.inventoryapplication.manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.inventoryapplication.R;

public class ManagerLogin extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_login);
    }

    public void openManagerPage(View v) {
        Intent intent = new Intent(this, Manager.class);
        startActivity(intent);
    }
}