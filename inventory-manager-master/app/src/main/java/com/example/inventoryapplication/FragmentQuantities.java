package com.example.inventoryapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapplication.employee.Employee;
import com.example.inventoryapplication.employee.QuantitiesUpdateDialog;
import com.example.inventoryapplication.log.ItemLog;
import com.example.inventoryapplication.log.LogManager;
import com.example.inventoryapplication.manager.Manager;
import com.example.inventoryapplication.manager.QuantitiesEditDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentQuantities extends Fragment {
    private RecyclerView recyclerView;
    private QuantitiesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quantities, container, false);

        recyclerView = view.findViewById(R.id.item_quantities_recyclerview);
        recyclerView.setHasFixedSize(true);
        adapter = new QuantitiesAdapter(LogManager.localLogDatabase, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.item_quantities_searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        adapter.quantitiesPopupMenuListener = new QuantitiesAdapter.QuantitiesPopupMenuListener() {
            @Override
            public void onClickedEditLog(int positionToBeEdited, ItemLog logToBeEdited) {
                QuantitiesEditDialog dialog = new QuantitiesEditDialog(logToBeEdited);
                dialog.show(getParentFragmentManager(), "Edit Log");
            }

            @Override
            public void onClickedUpdateLog(int positionToBeUpdated, ItemLog logToBeUpdated) {
                QuantitiesUpdateDialog dialog = new QuantitiesUpdateDialog(logToBeUpdated);
                dialog.show(getParentFragmentManager(), "Update Log");
            }

            @Override
            public void onClickedViewDetails(ItemLog logToBeViewed) {
                if (getActivity() instanceof Manager) {
                    ((Manager)getActivity()).loadFragmentLogDetails(logToBeViewed);
                } else if (getActivity() instanceof Employee) {
                    ((Employee)getActivity()).loadFragmentLogDetails(logToBeViewed);
                }
            }
        };
        return view;
    }

    public static class QuantitiesAdapter extends RecyclerView.Adapter<QuantitiesAdapter.QuantitiesViewHolder> implements Filterable {
        private Activity currentActivity;
        private List<ItemLog> logList;
        private List<ItemLog> logListFull;
        private Filter filter;
        private QuantitiesPopupMenuListener quantitiesPopupMenuListener = new QuantitiesPopupMenuListener() {
            public void onClickedEditLog(int positionToBeEdited, ItemLog logToBeEdited) { }
            public void onClickedViewDetails(ItemLog logToBeViewed) { }
            public void onClickedUpdateLog(int positionToBeUpdated, ItemLog logToBeUpdated) { }
        };

        public interface QuantitiesPopupMenuListener {
            void onClickedEditLog(int positionToBeEdited, ItemLog logToBeEdited);
            void onClickedViewDetails(ItemLog logToBeViewed);
            void onClickedUpdateLog(int positionToBeUpdated, ItemLog logToBeUpdated);
        }

        public static class QuantitiesViewHolder extends RecyclerView.ViewHolder {
            public TextView textView1;
            public TextView textView2;
            public ImageView imageView;

            public QuantitiesViewHolder(@NonNull View itemView) {
                super(itemView);
                textView1 = itemView.findViewById(R.id.line1);
                textView2 = itemView.findViewById(R.id.line2);
                imageView = itemView.findViewById(R.id.edit_log);
            }
        }

        public QuantitiesAdapter(Map<String, ItemLog> logMap, Activity currentActivity) {
            this.currentActivity = currentActivity;
            this.logList = new ArrayList<>(logMap.values());
            logListFull = new ArrayList<>(logList);
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    List<ItemLog> filteredList = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        filteredList.addAll(logListFull);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();

                        for (ItemLog log : logListFull) {
                            if (log.getID().replace('_', ' ').toLowerCase().contains(filterPattern)) {
                                filteredList.add(log);
                            }
                        }
                    }
                    FilterResults results = new FilterResults();
                    results.values = filteredList;

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    logList.clear();
                    logList.addAll((List<ItemLog>)results.values);
                    notifyDataSetChanged();
                }
            };

            LogManager.setLogManagerListener(new LogManager.LogManagerListener() {
                @Override
                public void onCreatedLog(ItemLog createdLog) {
                    logList.add(createdLog);
                    logListFull.add(createdLog);
                    currentActivity.runOnUiThread(() -> notifyDataSetChanged());
                }

                @Override
                public void onModifiedLog(ItemLog oldLog, ItemLog modifiedLog) {
                    logList.remove(oldLog);
                    logListFull.remove(oldLog);
                    logList.add(modifiedLog);
                    logListFull.add(modifiedLog);
                    currentActivity.runOnUiThread(() -> notifyDataSetChanged());
                }

                @Override
                public void onDeletedLog(ItemLog deletedLog) {
                    logList.remove(deletedLog);
                    logListFull.remove(deletedLog);
                    currentActivity.runOnUiThread(() -> notifyDataSetChanged());
                }
            });
        }

        @NonNull
        @Override
        public QuantitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_cardview, parent, false);
            return new QuantitiesViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull QuantitiesViewHolder holder, int position) {
            ItemLog currentLog = logList.get(position);
            holder.textView1.setText(String.format("%s %s", currentLog.getType(), currentLog.getName()));
            if (currentLog.getActualAmt() == -1) {
                holder.textView2.setText(String.format("Current Stock: N/A | Required Stock: %s", currentLog.getMinAmt()));
            } else {
                holder.textView2.setText(String.format("Current Stock: %s | Required Stock: %s", currentLog.getActualAmt(), currentLog.getMinAmt()));
            }
            holder.imageView.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(holder.imageView.getContext(), holder.imageView);
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.edit_log:
                            quantitiesPopupMenuListener.onClickedEditLog(position, currentLog);
                            return true;
                        case R.id.delete_log:
                            LogManager.deleteLog(currentLog.getID());
                            return true;
                        case R.id.view_details:
                            quantitiesPopupMenuListener.onClickedViewDetails(currentLog);
                            return true;
                        case R.id.update_log:
                            quantitiesPopupMenuListener.onClickedUpdateLog(position, currentLog);
                            return true;
                        default:
                            return false;
                    }
                });
                if (currentActivity instanceof Employee) {
                    popup.inflate(R.menu.employee_log_cardview_popup);
                } else {
                    popup.inflate(R.menu.manager_log_cardview_popup);
                }
                popup.show();
            });
        }

        @Override
        public int getItemCount() {
            return logList.size();
        }

        @Override
        public Filter getFilter() {
            return filter;
        }
    }
}
