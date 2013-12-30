package com.check.v3.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.check.v3.data.PhotoInfoData;
import com.check.v3.data.QuickCheckListItemData;
import com.check.v3.data.QuickCheckRspData;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


public final class CommonHelper {

    private static final String TAG = "CommonHelper";
    
	private static final int MAX_BITMAP_SIZE = 512;

    public static ProgressBar createProgress(final Context context) {
        final ProgressBar p = new ProgressBar(context);
        p.setIndeterminate(true);
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                40, 40);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        p.setLayoutParams(lp);
        return p;
    }


    public static void hideKeyboard(final Context context, final EditText input) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    public static boolean isEmpty(final Collection<?> c) {
        return (c == null) || (c.size() == 0);
    }

    public static boolean isEmpty(final String str) {
        return (str == null) || str.equals("");
    }

    public static void notify(final Context context, final CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void notify(final Context context, final int resId) {
        Toast.makeText(context, context.getText(resId), Toast.LENGTH_SHORT)
                .show();
    }

    public static void setFullScreen(final Activity activity,
            final boolean fullscreen) {
        if (fullscreen) {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void setPortraitOrientation(final Activity activity,
            final boolean portrait) {
        if (portrait) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
    
    public static QuickCheckRspData convertQuickCheckListItemData2RspData(QuickCheckListItemData itemData){
    	QuickCheckRspData data = new QuickCheckRspData();
		data.setId(itemData.getId());
		data.setSubmitterId(itemData.getSubmitterId());
		data.setSubmitterName(itemData.getSubmitterName());
		data.setResponsiblePeronId(itemData.getResponsiblePeronId());
		data.setResponsiblePersonName(itemData.getResponsiblePersonName());
		data.setOrganizationId(itemData.getOrganizationId());
		data.setOrganizationName(itemData.getOrganizationName());
		data.setDeadline(itemData.getDeadline());
		data.setState(itemData.getState());
		data.setLevel(itemData.getLevel());
		data.setDescription(itemData.getDescription());
		data.setImages(itemData.getImages());
    	return data;
    }
    
	public static PhotoInfoData getPhotoFromUri(Context context, Uri uri, int size) {
		InputStream input = null;
		Bitmap bitmap = null;
		PhotoInfoData photoInfoData = null;

		if(size > MAX_BITMAP_SIZE){
			size = MAX_BITMAP_SIZE;
		}
		
		try {
			input = context.getContentResolver().openInputStream(uri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);
			input.close();

			// Compute the scale.
			int scale = 1;
			while ((options.outWidth / scale > size)
					|| (options.outHeight / scale > size)) {
				scale *= 2;
			}

			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;

			input = context.getContentResolver().openInputStream(uri);

			bitmap = BitmapFactory.decodeStream(input, null, options);
			String filename = getPhotoFilename(new Date());
			
			photoInfoData = saveBitmapToStorage(uri, bitmap, filename);
		} catch (IOException e) {
			Log.w("", e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					Log.w("", e);
				}
			}
		}
		return photoInfoData;
	}

	public static PhotoInfoData saveBitmapToStorage(Uri uri, Bitmap bm, String fileName) {
		
		PhotoInfoData photoInfoData = null;
		File sdPath = Environment.getExternalStorageDirectory();
		
		File fPath = new File(sdPath, "cloudCheck/");
		if (!fPath.canWrite()) {
			boolean res = fPath.mkdirs();
			Log.d(TAG, "Create dir result: " + res);
		} else {
			Log.d(TAG, "Can't create dir!");
		}
		File photoFile = new File(fPath, fileName);

		if (photoFile.exists()) {
			photoFile.delete();
		}
		
		try {
			FileOutputStream out = new FileOutputStream(photoFile);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			Log.i(TAG, "已经保存");

			if (photoFile != null) {
				Log.i(TAG, "add file item: " + photoFile.getName() + ", size = "
						+ photoFile.length());
				Log.i(TAG, "add file item: " + photoFile.getAbsolutePath());
				photoInfoData = new PhotoInfoData(uri, photoFile, bm);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return photoInfoData;

	}
	
	public static String getPhotoFilename(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddKms");
		return dateFormat.format(date) + ".jpg";
	}

}
