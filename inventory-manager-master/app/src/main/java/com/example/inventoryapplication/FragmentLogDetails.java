package com.example.inventoryapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inventoryapplication.employee.Employee;
import com.example.inventoryapplication.employee.QuantitiesUpdateDialog;
import com.example.inventoryapplication.log.ItemLog;
import com.example.inventoryapplication.log.LogManager;
import com.example.inventoryapplication.manager.Manager;
import com.example.inventoryapplication.manager.QuantitiesEditDialog;

public class FragmentLogDetails extends Fragment {
    private ItemLog log;

    public FragmentLogDetails(ItemLog log) {
        this.log = log;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logdetails, container,false);
        if (getActivity() instanceof Manager) {
            view.findViewById(R.id.logdetails_update_log).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.logdetails_edit_log).setOnClickListener(v -> {
                QuantitiesEditDialog dialog = new QuantitiesEditDialog(log);
                dialog.show(getParentFragmentManager(), "Edit Log");
            });
            view.findViewById(R.id.logdetails_delete_log).setOnClickListener(v -> {
                LogManager.deleteLog(log.getID());
                getParentFragmentManager().beginTransaction().replace(R.id.manager_fragment_container, new FragmentQuantities()).commit();
            });
        } else if (getActivity() instanceof Employee) {
            view.findViewById(R.id.logdetails_edit_log).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.logdetails_delete_log).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.logdetails_update_log).setOnClickListener(v -> {
                QuantitiesUpdateDialog dialog = new QuantitiesUpdateDialog(log);
                dialog.show(getParentFragmentManager(), "Update Log");
            });
        }
        ((TextView)view.findViewById(R.id.logdetails_type)).setText(log.getType());
        ((TextView)view.findViewById(R.id.logdetails_name)).setText(log.getName());
        ((TextView)view.findViewById(R.id.logdetails_minamt)).setText(Integer.toString(log.getMinAmt()));
        if (log.getActualAmt() == -1) {
            ((TextView)view.findViewById(R.id.logdetails_actualamt)).setText("N/A");
        } else {
            ((TextView)view.findViewById(R.id.logdetails_actualamt)).setText(Integer.toString(log.getActualAmt()));
        }
        ((TextView)view.findViewById(R.id.logdetails_datecreated)).setText(log.getCreationDate());
        ((TextView)view.findViewById(R.id.logdetails_dateupdated)).setText(log.getLastUpdateDate());

        return view;
    }
}
