package com.check.v3.ui.dialog;

import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.check.v3.ui.dialog.ConfirmationDialogFragment.ConfirmationDialogFragmentListener;


public class DatePickerDialogFragment extends SherlockDialogFragment {
    protected static final String ARG_TITLE = "title";
    private static final String ARG_DIALOG_ID = "dialog_id";

    public static DatePickerDialogFragment newInstance(int dialogId, String title, String message) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_ID, dialogId);
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = "Choose a date";
        
        Calendar cd = Calendar.getInstance();
        Date date = new Date();
        cd.setTime(date);
        Dialog dialog = new DatePickerDialog(getActivity(), onDateSetListener,cd.get(Calendar.YEAR),cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH));
        dialog.setTitle(title);
        return dialog;
    }

	DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Log.d("test in frag", "" + year + "年" + (monthOfYear + 1) + "月"
					+ dayOfMonth + "日");
			getListener().datePickerDialogOnDateSet(getDialogId(), year + "-" + monthOfYear + "-"
					+ dayOfMonth);
		}
	}; 
    
    @Override
    public void onCancel(DialogInterface dialog) {
    	super.onCancel(dialog);
        getListener().dialogCancelled(getDialogId());
    }
    
    private int getDialogId() {
        return getArguments().getInt(ARG_DIALOG_ID);
    }

    public interface DatePickerDialogFragmentListener {
        void dialogCancelled(int dialogId);
        void datePickerDialogOnDateSet(int dialogId, String dateStr);
    }
    
    private DatePickerDialogFragmentListener getListener() {
        try {
            return (DatePickerDialogFragmentListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().getClass() +
                    " must implement DatePickerDialogFragmentListener");
        }
    }
}
