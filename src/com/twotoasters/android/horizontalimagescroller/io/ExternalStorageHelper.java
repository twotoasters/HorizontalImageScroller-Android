package com.twotoasters.android.horizontalimagescroller.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadUrlCacheKey;

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

	public static String getFilenameFromUrl(String urlString) {
		if (urlString == null) return null;
		int index = urlString.lastIndexOf('/');
		return (index  == -1) ? urlString : urlString.substring(index+1);
	}

	public static String UrlToFilename(ImageToLoadUrlCacheKey key) {
		if (key.getUrl() == null) return null;
		return key.getUrl().replace('/', '_').replace(':', '_').replace('?', '_');
	}

	public static File openFile(String dirname, String filename) {
		File f = null;
		File dir = openDirectory(dirname);
		if (dir != null) {
			f = new File(dir, filename);
		}
		return f;
	}

	public static File openDirectory(String dirname) {
		File f = null;
		if (isExternalStorageMounted()) {
			File storageDir = Environment.getExternalStorageDirectory();
			f = new File(storageDir, dirname);
			if (f != null && !f.exists()) {
				f.mkdirs();
			}
		}
		return f;
	}

	private static boolean isExternalStorageMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static File getCacheDir(Context context) {
		return isExternalStorageMounted() ? getExternalCacheDir(context) : getCacheDirByContext(context);
	}

	@SuppressLint("NewApi")
	private static File getExternalCacheDir(Context context) {
		synchronized(sSync) {
			return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) ?
					context.getExternalCacheDir() :
					context.getCacheDir();
		}
	}

	private static File getCacheDirByContext(Context context) {
		synchronized(sSync) {
			return context.getCacheDir();
		}
	}


}
