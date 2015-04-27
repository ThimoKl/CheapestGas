package de.einfachtanken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.DialogFragment;

/**
 * Created by thimokluser on 4/22/15.
 */
public class FuelChooserDialogFragment extends DialogFragment {
    public interface FuelChooserDialogListener {
        public void onFuelChosen(String fuelType, String fuelName);
    }

    FuelChooserDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (FuelChooserDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] items = new String[] {
                "E10", "E5", "Diesel"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        mListener.onFuelChosen("e10", "E10");
                        break;
                    case 1:
                        mListener.onFuelChosen("e5", "E5");
                        break;
                    case 2:
                        mListener.onFuelChosen("diesel", "Diesel");
                        break;
                }
            }
        });
        return builder.create();
    }
}
