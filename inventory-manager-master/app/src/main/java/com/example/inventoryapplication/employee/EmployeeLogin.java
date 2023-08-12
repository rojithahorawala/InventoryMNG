package com.example.inventoryapplication.employee;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;

        import com.example.inventoryapplication.R;


public class EmployeeLogin extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);
    }

    public void openEmployeePage(View v) {
        Intent intent = new Intent(this, Employee.class);
        startActivity(intent);
    }
}