package com.example.saintseiya.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.saintseiya.model.InventoryItem;
import java.util.List;

public class InventoryDialogFragment extends DialogFragment {
    private OnItemSelectedListener listener;
    private List<InventoryItem> inventoryItems;

    public interface OnItemSelectedListener {
        void onItemSelected(InventoryItem item);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public void setInventoryItems(List<InventoryItem> items) {
        this.inventoryItems = items;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] itemNames = new String[inventoryItems.size()];

        for (int i = 0; i < inventoryItems.size(); i++) {
            InventoryItem item = inventoryItems.get(i);
            itemNames[i] = item.getName() + " (x" + item.getQuantity() + ")";
        }

        builder.setTitle("Inventaire")
               .setItems(itemNames, (dialog, which) -> {
                   if (listener != null) {
                       listener.onItemSelected(inventoryItems.get(which));
                   }
               });
        return builder.create();
    }
}
