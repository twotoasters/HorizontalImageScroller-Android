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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.google.common.base.Preconditions;
import com.twotoasters.android.horizontalimagescroller.image.BitmapHelper;
import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadUrl;
import com.twotoasters.android.horizontalimagescroller.image.OnImageLoadedListener;

public class ImageCacheManager {

	final static String TAG = "ImageCacheManager";
	final static int N_THREADS = 3;
	final protected static int BIND_FROM_CACHE_MSG = 0;
	final protected static int BIND_DRAWABLE_MSG = 1;
	private static final int MAX_RETRIES = 3;

	final MemoryCache memoryCache = new MemoryCache();
	final ImagesQueue imageQueue = new ImagesQueue();
	final ImagesLoader[] imageLoaderThreads = new ImagesLoader[N_THREADS];
	final Map<ImageView, ImageUrlRequestCacheKey> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, ImageUrlRequestCacheKey>());

	Context context;

	static volatile ImageCacheManager instance;

	public static ImageCacheManager getInstance(Context context) {
		if(instance == null) {
			instance = new ImageCacheManager(context.getApplicationContext());
		}
		return instance;
	}

	protected ImageCacheManager(Context context) {
		this.context = context;
		for(int i = 0; i < N_THREADS; i++) {
			imageLoaderThreads[i] = new ImagesLoader();
			imageLoaderThreads[i].setPriority(Thread.NORM_PRIORITY - 1);
		}
	}

	public void unbindImage(ImageView imageView) {
		synchronized (imageViews) {
			imageViews.remove(imageView);
		}

		synchronized (imageQueue.imagesToLoad) {
			List<ImageUrlRequest> toRemove = new ArrayList<ImageUrlRequest>();
			for(ImageUrlRequest loader : imageQueue.imagesToLoad) {
				if(loader.getImageToLoadUrl().getImageView() == imageView) {
					toRemove.add(loader);
				}
			}

			for(ImageUrlRequest remove : toRemove) {
				imageQueue.imagesToLoad.remove(remove);
			}
		}
	}

	public void unbindListener(OnImageLoadedListener listener) {
		synchronized (imageQueue.imagesToLoad) {
			List<ImageUrlRequest> toRemove = new ArrayList<ImageUrlRequest>();
			for(ImageUrlRequest loader : imageQueue.imagesToLoad) {
				if(loader.getImageToLoadUrl().getOnImageLoadedListener() == listener) {
					toRemove.add(loader);
				}
			}

			for(ImageUrlRequest remove : toRemove) {
				imageQueue.imagesToLoad.remove(remove);
			}
		}
	}
	
	public boolean bindDrawable(ImageUrlRequest imageUrlRequest) {
		// BJD - clear out any previous instances of this ImageView so that we don't get the wrong
		// image showing up in cases where we request an image, scroll an adapter back up so the 
		// ImageView gets recycled and show a cached image, then have the network loaded version 
		// pop in later.
		ImageToLoadUrl imageToLoadUrl = imageUrlRequest.getImageToLoadUrl();
		imageViews.remove(imageToLoadUrl.getImageView()); // deluxe
		OnImageLoadedListener onImageLoadedListener = imageToLoadUrl.getOnImageLoadedListener();
		ImageUrlRequestCacheKey key = imageUrlRequest.getCacheKey();
		if(isMapped(key)) {
			bindFromMap(key, imageToLoadUrl.getImageView());
			if(onImageLoadedListener != null) {
				onImageLoadedListener.onImageLoaded(imageToLoadUrl);
			}
			return false;
		} else if(isCached(imageUrlRequest)) {
			Bitmap bm = getBitmapFromCache(imageUrlRequest);
			if(bm != null) {
				imageToLoadUrl.getImageView().setImageBitmap(bm);
				if(onImageLoadedListener != null) {
					onImageLoadedListener.onImageLoaded(imageToLoadUrl);
				}
				return false;
			}
		}

		imageViews.put(imageToLoadUrl.getImageView(), imageUrlRequest.getCacheKey());
		fetchAndBind(imageUrlRequest);
		return true;
	}

	public boolean isMapped(ImageUrlRequestCacheKey key) {
		return memoryCache.contains(key);
	}

	private void bindFromMap(ImageUrlRequestCacheKey key, ImageView imageView) {
		imageView.setImageBitmap(memoryCache.get(key));
	}
	
	public boolean isCached(ImageUrlRequest imageUrlRequest) {
		try {
			return openImageFileByUrl(imageUrlRequest).exists();
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	public File openImageFileByUrl(ImageUrlRequest imageUrlRequest) throws FileNotFoundException {
		Preconditions.checkState(imageUrlRequest.getCacheFileName() != null);
		File dir = ExternalStorageHelper.openDirectory(context);
		if(dir != null) {
			return new File(dir, imageUrlRequest.getCacheFileName());
		}
		throw new FileNotFoundException();
	}
	
	private Bitmap getBitmapFromCache(ImageUrlRequest imageUrlRequest) {
		Bitmap bitmap = memoryCache.get(imageUrlRequest.getCacheKey());
		if(bitmap == null) {
			bitmap = getBitmapFromFileCache(imageUrlRequest);
		}
		return bitmap;
	}
	
	private Bitmap getBitmapFromFileCache(ImageUrlRequest imageUrlRequest) {
		Bitmap bitmap = null;
		try {
			File f = openImageFileByUrl(imageUrlRequest);
			bitmap = decodeBitmap(f.getAbsolutePath(), imageUrlRequest.getReqWidth(), imageUrlRequest.getReqHeight(), true);
		} catch (FileNotFoundException e) {
		}
		return bitmap;
	}

	private Bitmap decodeBitmap(String filePath, int reqWidth, int reqHeight, boolean resampleIsUnnecessary) throws FileNotFoundException {
		return decodeBitmap(new FileInputStream(filePath), reqWidth, reqHeight, resampleIsUnnecessary);
	}

	private Bitmap decodeBitmap(InputStream is, int reqWidth, int reqHeight, boolean resampleIsUnnecessary) {
		Bitmap bitmap = null;
		try {
			if (resampleIsUnnecessary) {
				bitmap = BitmapFactory.decodeStream(is);
			} else {
				bitmap = BitmapHelper.decodeSampledBitmapFromSteam(new FlushedInputStream(is), reqWidth, reqHeight);
			}
		} catch (OutOfMemoryError e) {
			Log.w(TAG, "Out of memory while decoding bitmap stream");
			System.gc();
		}
		return bitmap;
	}

	private void fetchAndBind(final ImageUrlRequest imageUrlRequest) {
		imageQueue.clean(imageUrlRequest.getImageToLoadUrl().getImageView());
		synchronized (imageQueue.imagesToLoad) {
			if(imageUrlRequest.getImageToLoadUrl().isPriority()) {
				imageQueue.imagesToLoad.add(imageUrlRequest);
			} else {
				imageQueue.imagesToLoad.add(0, imageUrlRequest);
			}
			imageQueue.imagesToLoad.notifyAll();
		}

		for(int i = 0; i < N_THREADS; i++) {
			if(imageLoaderThreads[i].getState() == Thread.State.NEW) {
				imageLoaderThreads[i].start();
			}
		}
	}

	public void pleaseCacheDrawable(final ImageUrlRequest imageUrlRequest) {
		if(!isCached(imageUrlRequest)) {
			fetchAndCache(imageUrlRequest);
		}
	}

	protected void fetchAndCache(ImageUrlRequest imageUrlRequest) {
		try {
			InputStream is = fetch(imageUrlRequest);
			putBitmapToCaches(is, imageUrlRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected InputStream fetch(ImageUrlRequest imageUrlRequest) throws MalformedURLException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		ImageToLoadUrl imageToLoadUrl = imageUrlRequest.getImageToLoadUrl(); 
		httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(imageToLoadUrl.getUsername(), imageToLoadUrl.getPassword()));
		HttpResponse response = httpClient.execute(new HttpGet(imageToLoadUrl.getUrl()));
		int statusCode = response.getStatusLine().getStatusCode();
		String reason = response.getStatusLine().getReasonPhrase();
		if(statusCode > 299) {
			throw new HttpResponseException(statusCode, reason);
		}
		BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
		return entity.getContent();
	}

	private void putBitmapToCaches(InputStream is, ImageUrlRequest imageUrlRequest) throws IOException {
		FlushedInputStream fis = new FlushedInputStream(is);
		Bitmap bitmap = null;
		try {
			bitmap = BitmapHelper.decodeSampledBitmapFromSteam(fis, imageUrlRequest.getReqWidth(), imageUrlRequest.getReqHeight());
			memoryCache.put(imageUrlRequest.getCacheKey(), bitmap);
		} catch (OutOfMemoryError e) {
			Log.v(TAG, "writeToExternalStorage - Out of memory");
			System.gc();
		}

		if(bitmap != null) {
			createFileIfNonexistent(imageUrlRequest);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(openImageFileByUrl(imageUrlRequest)), 65535);
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		}
		fis.close();
		is.close();
	}

	private void createFileIfNonexistent(ImageUrlRequest imageUrlRequest) {
		try {
			if(openImageFileByUrl(imageUrlRequest).exists() == false) {
				createFile(imageUrlRequest);
			}
		} catch (FileNotFoundException e) {
			createFile(imageUrlRequest);
		}
	}

	private void createFile(ImageUrlRequest imageUrlRequest) {
		try {
			openImageFileByUrl(imageUrlRequest).createNewFile();
		} catch (IOException e) {
			Log.e(TAG, "File creation failed: " + e);
		}
	}

	class ImagesQueue {
		private Vector<ImageUrlRequest> imagesToLoad = new Vector<ImageUrlRequest>();

		public void clean(ImageView imageView) {
			// must synchronize over the vector, else someone else could be
			// deleting from the vector and cause an index out of bounds
			// exception
			synchronized (imagesToLoad) {
				for(int j = 0; j < imagesToLoad.size();) {
					if(imagesToLoad.get(j).getImageToLoadUrl().getImageView() == imageView) {
						imagesToLoad.remove(j);
					} else
						++j;
				}
			}
		}
	}

	class ImagesLoader extends Thread {
		@Override
		public void run() {
			try {
				while(true) {
					// thread waits until there are any images to load in the queue
					if(imageQueue.imagesToLoad.size() == 0)
						synchronized (imageQueue.imagesToLoad) {
							imageQueue.imagesToLoad.wait();
						}
					if(imageQueue.imagesToLoad.size() != 0) {
						ImageUrlRequest imageUrlRequest;
						synchronized (imageQueue.imagesToLoad) {
							try {
								imageUrlRequest = imageQueue.imagesToLoad.lastElement();
								imageQueue.imagesToLoad.remove(imageQueue.imagesToLoad.size() - 1);
							} catch (Exception e) {
								continue;
							}
						}

						Bitmap bitmap = null;
						if(isCached(imageUrlRequest)) {
							bitmap = getBitmapFromFileCache(imageUrlRequest);
						}

						if(bitmap == null) {
							bitmap = fetchBitmap(imageUrlRequest);
						}

						if(bitmap != null) {
							memoryCache.putIfAbsent(imageUrlRequest.getCacheKey(), bitmap);
						}
						ImageUrlRequestCacheKey key = imageViews.get(imageUrlRequest.getImageToLoadUrl().getImageView());
						if(key != null && key.equals(imageUrlRequest.getCacheKey())) {
							ImageViewUpdater updater = new ImageViewUpdater(bitmap, imageUrlRequest);
							Activity activity = (Activity)imageUrlRequest.getImageToLoadUrl().getImageView().getContext();
							if(activity != null) {
								activity.runOnUiThread(updater);
							}
						}
					}
					if(Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}
	}

	private Bitmap fetchBitmap(ImageUrlRequest imageUrlRequest) {
		Bitmap bitmap = null;
		InputStream is = null;
		int retries = MAX_RETRIES;
		do {
			try {
				is = fetch(imageUrlRequest);
				if(imageUrlRequest.getImageToLoadUrl().isCanCacheFile()) {
					putBitmapToCaches(is, imageUrlRequest);
					bitmap = memoryCache.get(imageUrlRequest.getCacheKey());
					getBitmapFromCache(imageUrlRequest);
				} else {
					bitmap = decodeBitmap(is, imageUrlRequest.getReqWidth(), imageUrlRequest.getReqHeight(), true);
				}
			} catch (Exception e) {
				Log.v(TAG, "fetchDrawable - Exception: " + imageUrlRequest.getCacheKey().toString());
				e.printStackTrace();
			}

			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} while((--retries > 0) && (bitmap == null));

		return bitmap;
	}

	class ImageViewUpdater implements Runnable {
		Bitmap bitmap;
		ImageUrlRequest imageUrlRequest;

		public ImageViewUpdater(Bitmap bitmap, ImageUrlRequest imageUrlRequest) {
			this.bitmap = bitmap;
			this.imageUrlRequest = imageUrlRequest;
		}

		@Override
		public void run() {
			ImageToLoadUrl imageToLoadUrl = imageUrlRequest.getImageToLoadUrl();
			if(bitmap != null) {
				imageToLoadUrl.getImageView().setImageBitmap(bitmap);
				if(imageToLoadUrl.getOnImageLoadedListener() != null) {
					imageToLoadUrl.getOnImageLoadedListener().onImageLoaded(imageToLoadUrl);
				}
			} else {
				if (imageUrlRequest.getImageFailedToLoadResourceId() != 0) {
					Resources res = imageToLoadUrl.getImageView().getContext().getResources();
					int imageFailedToLoadResourceId = imageUrlRequest.getImageFailedToLoadResourceId();
					int width = imageUrlRequest.getReqWidth();
					int height = imageUrlRequest.getReqHeight();
					ImageView imageView = imageToLoadUrl.getImageView();
					BitmapHelper.applySampledResourceToImageView(res, imageFailedToLoadResourceId, width, height, imageView);
				}
				if(imageToLoadUrl.getOnImageLoadedListener() != null) {
					imageToLoadUrl.getOnImageLoadedListener().onLoadFailure(imageToLoadUrl);
				}
			}
		}
	}

	public void clearMemoryCache() {
		memoryCache.clear();
	}

	public void clearFileCache() {
		File dir = ExternalStorageHelper.getExternalCacheDir(context);
		File files[] = dir.listFiles();
		for(File f : files) {
			f.delete();
		}
	}

}
