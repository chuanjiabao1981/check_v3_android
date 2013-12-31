package com.check.v3.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import com.actionbarsherlock.app.SherlockDialogFragment;


public class ListItemDialogFragment extends SherlockDialogFragment implements OnClickListener,
        OnCancelListener {

    private static final String ARG_DIALOG_ID = "dialog_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_LIST_ITEMS = "list_item";
    private static final String ARG_CANCEL_TEXT = "cancel";


    public static ListItemDialogFragment newInstance(int dialogId, String title, String[] items,
    		String cancelText) {
        ListItemDialogFragment fragment = new ListItemDialogFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_ID, dialogId);
        args.putString(ARG_TITLE, title);
        args.putStringArray(ARG_LIST_ITEMS, items);
        args.putString(ARG_CANCEL_TEXT, cancelText);
        fragment.setArguments(args);

        return fragment;
    }


    public interface ListItemDialogFragmentListener {
        void doNegativeClick(int dialogId);
        void dialogCancelled(int dialogId);
        void doListItemClick(int dialogId, int item);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String cancelText = args.getString(ARG_CANCEL_TEXT);        
        final String[] items = args.getStringArray(ARG_LIST_ITEMS); 

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        if(item == 0){
    	        	Log.d("item", "on selected 0");
    	        } else if(item == 1){
    	        	Log.d("item", "on selected 1");
    	        }
    	        getListener().doListItemClick(getDialogId(), item);
    	    }
    	});

        builder.setNegativeButton(cancelText, this);
        return builder.create();
    }
    
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE: {
                getListener().doNegativeClick(getDialogId());
                break;
            }
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getListener().dialogCancelled(getDialogId());
    }

    private int getDialogId() {
        return getArguments().getInt(ARG_DIALOG_ID);
    }

    private ListItemDialogFragmentListener getListener() {
        try {
            return (ListItemDialogFragmentListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().getClass() +
                    " must implement ConfirmationDialogFragmentListener");
        }
    }
}
