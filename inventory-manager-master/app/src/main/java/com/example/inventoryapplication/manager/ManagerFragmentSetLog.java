package com.example.inventoryapplication.manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inventoryapplication.R;
import com.example.inventoryapplication.log.LogManager;

public class ManagerFragmentSetLog extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manager_fragment_setlog, container,false);
        view.findViewById(R.id.manager_setlog_done).setOnClickListener(v -> {
            String type = ((EditText)view.findViewById(R.id.manager_setlog_type)).getText().toString();
            String name = ((EditText)view.findViewById(R.id.manager_setlog_name)).getText().toString();
            String min_amt = ((EditText)view.findViewById(R.id.manager_setlog_minamt)).getText().toString();

            if (type.isEmpty() || name.isEmpty() || min_amt.isEmpty()) {
                Toast.makeText(getActivity(), "Please input values properly", Toast.LENGTH_SHORT).show();
            } else {
                if (LogManager.createLog(type, name, Integer.parseInt(min_amt), -1)) {
                    if (getActivity() instanceof Manager) {
                        ((Manager) getActivity()).loadFragmentQuantities();
                    }
                }
            }
        });
        return view;
    }
}
