package com.check.v3.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.check.v3.ui.dialog.DatePickerDialogFragment.DatePickerDialogFragmentListener;


public class ProgressDialogFragment extends SherlockDialogFragment {
	private static final String ARG_DIALOG_ID = "dialog_id";
    protected static final String ARG_TITLE = "title";
    protected static final String ARG_MESSAGE = "message";

    public static ProgressDialogFragment newInstance(int dialogId, String title, String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_ID, dialogId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String message = args.getString(ARG_MESSAGE);

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if(!TextUtils.isEmpty(title)){
        	dialog.setTitle(title);
        }
        if(!TextUtils.isEmpty(message)){
        	dialog.setMessage(message);
        }

        return dialog;
    }

    private int getDialogId() {
        return getArguments().getInt(ARG_DIALOG_ID);
    }
    
    @Override
    public void onCancel(DialogInterface dialog) {
    	super.onCancel(dialog);
        getListener().dialogCancelled(getDialogId());
    }


    public interface ProgressDialogFragmentListener {
        void dialogCancelled(int dialogId);
    }
    
    private ProgressDialogFragmentListener getListener() {
        try {
            return (ProgressDialogFragmentListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().getClass() +
                    " must implement ProgressDialogFragmentListener");
        }
    }
}
