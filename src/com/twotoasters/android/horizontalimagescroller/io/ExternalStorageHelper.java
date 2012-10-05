/*
Copyright 2012 Two Toasters, LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
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
		File directory = null;
		if (storageDir != null) {
			// to make sure our files don't collide with yours
			directory = new File(storageDir, ExternalStorageHelper.class.getPackage().getName());
			
			if (directory != null && isExternalStorageMounted() && !directory.exists()) {
				directory.mkdirs();
			}
		}
		return directory;
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
