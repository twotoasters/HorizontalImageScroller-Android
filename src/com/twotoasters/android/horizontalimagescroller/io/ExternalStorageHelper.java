package com.twotoasters.android.horizontalimagescroller.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

public class ExternalStorageHelper {

	static Object sSync = new Object();

	public static String rawResourceToString(Context context, int resourceId) {
		InputStream is = context.getResources().openRawResource(resourceId);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writer.toString();
	}

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
