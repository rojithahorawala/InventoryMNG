package com.example.inventoryapplication.employee;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.example.inventoryapplication.ApplicationContext;
import com.example.inventoryapplication.FragmentLogDetails;
import com.example.inventoryapplication.FragmentQuantities;
import com.example.inventoryapplication.R;
import com.example.inventoryapplication.log.ItemLog;
import com.example.inventoryapplication.log.LogManager;
import com.example.inventoryapplication.manager.Manager;

public class QuantitiesUpdateDialog extends AppCompatDialogFragment {
    private EditText editTextActualAmt;
    private ItemLog log;

    public QuantitiesUpdateDialog(ItemLog log) {
        this.log = log;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_quantities_updatedialog, null);
        editTextActualAmt = view.findViewById(R.id.updatedialog_actualamount);

        builder.setView(view)
                .setTitle("Update Log")
                .setNegativeButton("Cancel", (dialog, which) -> { })
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String actualAmt = editTextActualAmt.getText().toString();
                    if (actualAmt.isEmpty()) {
                        ApplicationContext.showToast("Please input value properly");
                    } else {
                        LogManager.beginModification(log.getID());
                        LogManager.modifyActualAmt(Integer.parseInt(actualAmt));
                        LogManager.commitModification();
                        Fragment fragment = getParentFragmentManager().findFragmentByTag("FragmentLogDetails");
                        if (fragment != null && fragment.isVisible()) {
                            ((Employee)getActivity()).loadFragmentQuantities();
                        }
                    }
                });

        return builder.create();
    }
}
