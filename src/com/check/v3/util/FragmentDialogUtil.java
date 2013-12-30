package com.check.v3.util;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.check.client.R;
import com.check.v3.mainui.ConfirmationDialogFragment;
import com.check.v3.mainui.DatePickerDialogFragment;
import com.check.v3.mainui.ListItemDialogFragment;
import com.check.v3.mainui.ProgressDialogFragment;

public class FragmentDialogUtil {

	public static void showDialog(Fragment fragCtx, int dialogId) {
		DialogFragment fragment;
		switch (dialogId) {
		case R.id.dialog_confirm_delete: {
			String title = "delete?";
			String message = "Are you sure to delete?";
			String confirmText = "Ok";
			String cancelText = "Cancel";

			fragment = ConfirmationDialogFragment.newInstance(dialogId, title,
					message, confirmText, cancelText);
			break;
		}
		case R.id.dialog_choose_photo_list_item: {
			String title = "添加图片";
			String cancelText = "取消";
			String[] items = { "选取图片", "现场拍照" };

			fragment = ListItemDialogFragment.newInstance(dialogId, title,
					items, cancelText);
			break;
		}
		case R.id.dialog_date_picker: {
			fragment = DatePickerDialogFragment.newInstance(dialogId, "选择日期",
					"");
			break;
		}
		case R.id.dialog_show_progress: {
			String message = "网络通信中, 请稍等...";
			fragment = ProgressDialogFragment
					.newInstance(dialogId, "", message);
			break;
		}
		default: {
			throw new RuntimeException(
					"Called showDialog(int) with unknown dialog id.");
		}
		}

		fragment.setTargetFragment(fragCtx, dialogId);
		fragment.show(fragCtx.getFragmentManager(), getDialogTag(dialogId));
	}

	public static void removeDialog(Fragment fragCtx, int dialogId) {
		FragmentManager fm = fragCtx.getFragmentManager();

		if (fm == null || fragCtx.isRemoving() || fragCtx.isDetached()) {
			return;
		}

		// Make sure the "show dialog" transaction has been processed when we
		// call
		// findFragmentByTag() below. Otherwise the fragment won't be found and
		// the dialog will
		// never be dismissed.
		fm.executePendingTransactions();

		DialogFragment fragment = (DialogFragment) fm
				.findFragmentByTag(getDialogTag(dialogId));

		if (fragment != null) {
			fragment.dismiss();
		}
	}

	private static String getDialogTag(int dialogId) {
		return String.format("dialog-%d", dialogId);
	}
	
}
