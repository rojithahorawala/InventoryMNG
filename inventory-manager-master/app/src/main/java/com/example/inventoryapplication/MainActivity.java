package com.example.inventoryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.inventoryapplication.employee.EmployeeLogin;
import com.example.inventoryapplication.manager.ManagerLogin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openEmployee(View v) {
        Intent intent = new Intent(this, EmployeeLogin.class);
        startActivity(intent);
    }
    public void openManager(View v) {
        Intent intent = new Intent(this, ManagerLogin.class);
        startActivity(intent);
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return super.getSharedPreferences(name, mode);
    }

}