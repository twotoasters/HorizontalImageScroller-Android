package com.twotoasters.android.horizontalimagescroller.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

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

//	public static String UrlToDirName(ImageUrlRequest imageUrlRequest) {
//		String url = imageUrlRequest.getImageToLoadUrl().getUrl();
//		if (url == null || imageUrlRequest.getReqHeight() < 1 || imageUrlRequest.getReqWidth() < 1 ) return null;
//		return url.replace('/', '_').replace(':', '_').replace('?', '_');
//	}
	
	public static String UrlToFileName(ImageUrlRequest imageUrlRequest) {
		String result = null;
		try {
			MessageDigest hash = MessageDigest.getInstance("SHA-256");
			String url = imageUrlRequest.getImageToLoadUrl().getUrl();
			String username = imageUrlRequest.getImageToLoadUrl().getUsername();
			String password = imageUrlRequest.getImageToLoadUrl().getPassword();
			int width = imageUrlRequest.getReqWidth();
			int height = imageUrlRequest.getReqHeight();
			String toHash = String.format("url_%1$s_creds_%2$s%3$s_size_%4$dx%5$d", url, username, password, width, height);
			hash.update(toHash.getBytes());
			byte[] digest = hash.digest();
			char[] digestAsCharArray = Hex.encodeHex(digest);
			result = new String(digestAsCharArray);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
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
