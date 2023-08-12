package com.example.inventoryapplication;

import android.app.Application;
import android.widget.Toast;

import com.example.inventoryapplication.log.LogManager;
import com.example.inventoryapplication.networking.Client;

public class ApplicationContext extends Application {
    private static ApplicationContext instance;

    @Override
    public void onCreate() { //runs code when app is started
        instance = this;
        super.onCreate();
        Client.connectToServer();
    }

    public static void showToast(String text) {
        Toast.makeText(instance.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}