package com.twotoasters.android.horizontalimagescroller.io;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

public class ExternalStorageHelper {

	static Object sSync = new Object();

	public static File openDirectory(Context context) {
		File storageDir = getExternalCacheDir(context);
		if (isExternalStorageMounted()) {
			if (storageDir != null && !storageDir.exists()) {
				storageDir.mkdirs();
			}
		}
		return storageDir;
	}

	private static boolean isExternalStorageMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static File getCacheDir(Context context, ImageUrlRequest request) {
		return isExternalStorageMounted() ? getExternalCacheDir(context) : getCacheDirByContext(context);
	}

	@SuppressLint("NewApi")
	public static File getExternalCacheDir(Context context) {
		synchronized(sSync) {
			return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) ?
					context.getExternalCacheDir() :
					context.getCacheDir();
		}
	}

	public static File getCacheDirByContext(Context context) {
		synchronized(sSync) {
			return context.getCacheDir();
		}
	}


}
