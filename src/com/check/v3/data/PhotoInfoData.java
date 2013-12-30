package com.check.v3.data;

import java.io.File;
import java.io.Serializable;
import android.graphics.Bitmap;
import android.net.Uri;

public class PhotoInfoData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2964361737622240121L;

	
	private Uri photoUri;
	private File photoFile;
	private Bitmap photoBitmap;
	
	public PhotoInfoData(Uri photoUri, File photoFile, Bitmap photoBitmap) {
		super();
		this.photoUri = photoUri;
		this.photoFile = photoFile;
		this.photoBitmap = photoBitmap;
	}

	public Uri getPhotoUri() {
		return photoUri;
	}
	
	public void setPhotoUri(Uri photoUri) {
		this.photoUri = photoUri;
	}

	public File getPhotoFile() {
		return photoFile;
	}

	public void setPhotoFile(File photoFile) {
		this.photoFile = photoFile;
	}

	public Bitmap getPhotoBitmap() {
		return photoBitmap;
	}
	
	public void setPhotoBitmap(Bitmap photoBitmap) {
		this.photoBitmap = photoBitmap;
	}
}
