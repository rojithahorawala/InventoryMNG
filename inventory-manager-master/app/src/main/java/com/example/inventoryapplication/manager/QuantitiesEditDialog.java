package com.example.inventoryapplication.manager;

import android.app.Dialog;
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
import com.example.inventoryapplication.R;
import com.example.inventoryapplication.log.ItemLog;
import com.example.inventoryapplication.log.LogManager;

import java.util.HashMap;

public class QuantitiesEditDialog extends AppCompatDialogFragment {
    private ItemLog log;
    private EditText editTextType;
    private EditText editTextName;
    private EditText editTextMinAmt;
    private EditText editTextActualAmt;

    public QuantitiesEditDialog(ItemLog log) {
        this.log = log;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_quantities_editdialog, null);
        editTextType = view.findViewById(R.id.editdialog_type);
        editTextName = view.findViewById(R.id.editdialog_name);
        editTextMinAmt = view.findViewById(R.id.editdialog_minamount);
        editTextActualAmt = view.findViewById(R.id.editdialog_actualamount);
        editTextType.setText(log.getType());
        editTextName.setText(log.getName());
        editTextMinAmt.setText(Integer.toString(log.getMinAmt()));
        if (log.getActualAmt() == -1) {
            editTextActualAmt.setText("");
        } else {
            editTextActualAmt.setText(Integer.toString(log.getActualAmt()));
        }

        builder.setView(view)
                .setTitle("Edit Log")
                .setNegativeButton("Cancel", (dialog, which) -> {})
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String type = editTextType.getText().toString();
                    String name = editTextName.getText().toString();
                    String minAmt = editTextMinAmt.getText().toString();
                    String actualAmt = editTextActualAmt.getText().toString();
                    HashMap<String, ItemLog> temp = new HashMap<>(LogManager.localLogDatabase);
                    temp.remove(log.getID());
                    if (temp.containsKey(type + "_" + name)) {
                        ApplicationContext.showToast("There is already another log with the same type and name");
                        return;
                    }
                    if (type.isEmpty() || name.isEmpty() || minAmt.isEmpty()) {
                        ApplicationContext.showToast("Please input values properly");
                    } else {
                        if (actualAmt.isEmpty()) actualAmt = "-1";
                        LogManager.overhaulLog(log.getID(), type, name, Integer.parseInt(minAmt), Integer.parseInt(actualAmt));

                        Fragment fragment = getParentFragmentManager().findFragmentByTag("FragmentLogDetails");
                        if (fragment != null && fragment.isVisible()) {
                            ((Manager)getActivity()).loadFragmentQuantities();
                        }
                    }
                });

        return builder.create();
    }
}
