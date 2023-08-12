package com.example.inventoryapplication.employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.inventoryapplication.FragmentLogDetails;
import com.example.inventoryapplication.FragmentQuantities;
import com.example.inventoryapplication.R;
import com.example.inventoryapplication.log.ItemLog;
import com.google.android.material.navigation.NavigationView;

public class Employee extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        toolbar = findViewById(R.id.employee_toolbar);
        setSupportActionBar(toolbar); //sets the activity's toolbar to the one we made

        drawerLayout = findViewById(R.id.employee_drawer);
        navigationView = findViewById(R.id.employee_navigationView);
        navigationView.setNavigationItemSelectedListener(this); //sets this activity to listen for menu item's being clicked in the drawer (needed to use onNavigationItemSelected)

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close); //creates the actually drawer instance using the layout and toolbar we created
        drawerLayout.addDrawerListener(actionBarDrawerToggle); //sets our layout to use this new drawer instance
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true); //enables the drawer to be used by creating the 3 lined menu icon on the top left
        actionBarDrawerToggle.syncState(); //Syncs the state of the drawer to the layout

        //load default fragment
        fragmentTransaction = getSupportFragmentManager().beginTransaction(); //We begin a new transaction through the FragmentManager (FragmentTransactions are used to manipulate fragments)
        fragmentTransaction.add(R.id.employee_fragment_container, new FragmentQuantities(), "FragmentQuantities"); //Create and add our first fragment into our fragment container
        fragmentTransaction.commit(); //Commits the changes we made
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { //is called when an item in the menu is clicked on
        drawerLayout.closeDrawer(GravityCompat.START); //closes the drawer
        switch (item.getItemId()) { //tests to see which item in the drawer was clicked on in order to execute the required code
            case R.id.all_logs:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.employee_fragment_container, new FragmentQuantities(), "FragmentQuantities");
                fragmentTransaction.commit();
                break;
        }
        return true;
    }

    public void loadFragmentQuantities() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.employee_fragment_container, new FragmentQuantities(), "FragmentQuantities");
        fragmentTransaction.commit();
    }

    public void loadFragmentLogDetails(ItemLog log) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.employee_fragment_container, new FragmentLogDetails(log), "FragmentLogDetails");
        fragmentTransaction.commit();
    }
}