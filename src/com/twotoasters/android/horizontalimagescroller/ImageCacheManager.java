package com.twotoasters.android.horizontalimagescroller;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.google.common.base.Preconditions;

public class ImageCacheManager {

	final static String TAG = "ImageCacheManager";
	final static int N_THREADS = 3;
	final protected static int BIND_FROM_CACHE_MSG = 0;
	final protected static int BIND_DRAWABLE_MSG = 1;
	private static final int MAX_RETRIES = 3;

	final MemoryCache memoryCache = new MemoryCache();
	final ImagesQueue imageQueue = new ImagesQueue();
	final ImagesLoader[] imageLoaderThreads = new ImagesLoader[N_THREADS];
	final Map<ImageView, ImageToLoadUrlCacheKey> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, ImageToLoadUrlCacheKey>());

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
			List<ImageToLoad> toRemove = new ArrayList<ImageToLoad>();
			for(ImageToLoad loader : imageQueue.imagesToLoad) {
				if(loader.imageView == imageView) {
					toRemove.add(loader);
				}
			}

			for(ImageToLoad remove : toRemove) {
				imageQueue.imagesToLoad.remove(remove);
			}
		}
	}

	public void unbindListener(OnImageLoadedListener listener) {
		synchronized (imageQueue.imagesToLoad) {
			List<ImageToLoadUrl> toRemove = new ArrayList<ImageToLoadUrl>();
			for(ImageToLoadUrl loader : imageQueue.imagesToLoad) {
				if(loader.getOnImageLoadedListener() == listener) {
					toRemove.add(loader);
				}
			}

			for(ImageToLoad remove : toRemove) {
				imageQueue.imagesToLoad.remove(remove);
			}
		}
	}
	
	public boolean bindDrawable(ImageToLoadUrl imageToLoadUrl) {
		// BJD - clear out any previous instances of this ImageView so that we
		// don't get the
		// wrong image showing up in cases where we request an image, scroll an
		// adapter back
		// up so the ImageView gets recycled and show a cached image, then have
		// the network
		// loaded version pop in later.
		imageViews.remove(imageToLoadUrl.getImageView()); // deluxe
		OnImageLoadedListener onImageLoadedListener = imageToLoadUrl.getOnImageLoadedListener();
		ImageToLoadUrlCacheKey key = imageToLoadUrl.toCacheKey();
		if(isMapped(key)) {
			bindFromMap(key, imageToLoadUrl.getImageView());
			if(onImageLoadedListener != null) {
				onImageLoadedListener.onImageLoaded(imageToLoadUrl.getUrl());
			}
			return false;
		} else if(isCached(key)) {
			Bitmap bm = getBitmapFromFileCache(key);
			if(bm != null) {
				imageToLoadUrl.getImageView().setImageBitmap(bm);
				if(onImageLoadedListener != null) {
					onImageLoadedListener.onImageLoaded(imageToLoadUrl.getUrl());
				}
				return false;
			}
		}

		imageViews.put(imageToLoadUrl.getImageView(), key);
		fetchAndBind(imageToLoadUrl);
		return true;
	}

	public boolean isMapped(ImageToLoadUrlCacheKey id) {
		return memoryCache.contains(id);
	}

	private void bindFromMap(ImageToLoadUrlCacheKey id, ImageView imageView) {
		imageView.setImageBitmap(memoryCache.get(id));
	}
	
	private boolean isCached(ImageToLoadUrl imageToLoadUrl) {
		return isCached(imageToLoadUrl.toCacheKey());
	}
	
	private boolean isCached(ImageToLoadUrlCacheKey key) {
		try {
			return openImageFileByUrl(key).exists();
		} catch (FileNotFoundException e) {
			return false;
		}
	}

	public File openImageFileByUrl(ImageToLoadUrlCacheKey key) throws FileNotFoundException {
		return openImageFile(ExternalStorageHelper.UrlToFilename(key));
	}

	private File openImageFile(String filename) throws FileNotFoundException {
		Preconditions.checkState(filename != null);

		File dir = ExternalStorageHelper.getCacheDir(context);
		if(dir != null) {
			return new File(dir, filename);
		}
		throw new FileNotFoundException();
	}
	
	private Bitmap getBitmapFromCache(ImageToLoadUrl imageToLoadUrl) {
		return getBitmapFromCache(imageToLoadUrl.toCacheKey());
	}

	private Bitmap getBitmapFromCache(ImageToLoadUrlCacheKey key) {
		Bitmap bitmap = memoryCache.get(key);
		if(bitmap == null) {
			bitmap = getBitmapFromFileCache(key);
		}
		return bitmap;
	}
	
	private Bitmap getBitmapFromFileCache(ImageToLoadUrl imageToLoadUrl) {
		return getBitmapFromFileCache(imageToLoadUrl.toCacheKey());
	}

	private Bitmap getBitmapFromFileCache(ImageToLoadUrlCacheKey key) {
		Bitmap bitmap = null;
		try {
			File f = openImageFileByUrl(key);
			bitmap = decodeBitmap(f.getAbsolutePath());
		} catch (FileNotFoundException e) {
		}
		return bitmap;
	}

	private Bitmap decodeBitmap(String filePath) throws FileNotFoundException {
		return decodeBitmap(new FileInputStream(filePath));
	}

	private Bitmap decodeBitmap(InputStream is) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(new FlushedInputStream(is));
		} catch (OutOfMemoryError e) {
			Log.w(TAG, "Out of memory while decoding bitmap stream");
			System.gc();
		}
		return bitmap;
	}

	private void fetchAndBind(final ImageToLoadUrl imageToLoadUrl) {
		imageQueue.clean(imageToLoadUrl.getImageView());
		synchronized (imageQueue.imagesToLoad) {
			if(imageToLoadUrl.isPriority()) {
				imageQueue.imagesToLoad.add(imageToLoadUrl);
			} else {
				imageQueue.imagesToLoad.add(0, imageToLoadUrl);
			}
			imageQueue.imagesToLoad.notifyAll();
		}

		for(int i = 0; i < N_THREADS; i++) {
			if(imageLoaderThreads[i].getState() == Thread.State.NEW) {
				imageLoaderThreads[i].start();
			}
		}
	}

	public void pleaseCacheDrawable(final ImageToLoadUrl imageToLoadUrl) {
		if(!isCached(imageToLoadUrl.toCacheKey())) {
			fetchAndCache(imageToLoadUrl);
		}
	}

	protected void fetchAndCache(ImageToLoadUrl imageToLoadUrl) {
		try {
			InputStream is = fetch(imageToLoadUrl);
			putBitmapToCaches(is, imageToLoadUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected InputStream fetch(ImageToLoadUrl imageToLoadUrl) throws MalformedURLException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
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

	private void putBitmapToCaches(InputStream is, ImageToLoadUrl imageToLoadUrl) throws IOException {
		FlushedInputStream fis = new FlushedInputStream(is);
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(fis);
			memoryCache.put(imageToLoadUrl.toCacheKey(), bitmap);
		} catch (OutOfMemoryError e) {
			Log.v(TAG, "writeToExternalStorage - Out of memory");
			System.gc();
		}

		if(bitmap != null) {
			String filename = ExternalStorageHelper.UrlToFilename(imageToLoadUrl.toCacheKey());
			createFileIfNonexistent(filename);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(openImageFile(filename)), 65535);
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		}
		fis.close();
		is.close();
	}

	private void createFileIfNonexistent(String filename) {
		try {
			if(openImageFile(filename).exists()) {
				return;
			}
		} catch (FileNotFoundException e) {
			createFile(filename);
		}
	}

	private void createFile(String filename) {
		try {
			openImageFile(filename).createNewFile();
		} catch (IOException e) {
			Log.e(TAG, "File creation failed: " + e);
		}
	}

	class ImagesQueue {
		private Vector<ImageToLoadUrl> imagesToLoad = new Vector<ImageToLoadUrl>();

		public void clean(ImageView imageView) {
			// must synchronize over the vector, else someone else could be
			// deleting from the vector and cause an index out of bounds
			// exception
			synchronized (imagesToLoad) {
				for(int j = 0; j < imagesToLoad.size();) {
					if(imagesToLoad.get(j).getImageView() == imageView) {
						// Log.v(TAG, "Cleaning an queued imageView request");
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
					// thread waits until there are any images to load in the
					// queue
					if(imageQueue.imagesToLoad.size() == 0)
						synchronized (imageQueue.imagesToLoad) {
							imageQueue.imagesToLoad.wait();
						}
					if(imageQueue.imagesToLoad.size() != 0) {
						ImageToLoadUrl imageToLoad;
						synchronized (imageQueue.imagesToLoad) {
							try {
								imageToLoad = imageQueue.imagesToLoad.lastElement();
								imageQueue.imagesToLoad.remove(imageQueue.imagesToLoad.size() - 1);
							} catch (Exception e) {
								continue;
							}
						}

						Bitmap bitmap = null;
						if(isCached(imageToLoad.toCacheKey())) {
							bitmap = getBitmapFromFileCache(imageToLoad);
						}

						if(bitmap == null) {
							bitmap = fetchBitmap(imageToLoad);
						}

						if(bitmap != null) {
							memoryCache.putIfAbsent(imageToLoad.toCacheKey(), bitmap);
						}
						ImageToLoadUrlCacheKey key = imageViews.get(imageToLoad.getImageView());
						if(key != null && key.equals(imageToLoad.toCacheKey())) {
							ImageViewUpdater updater = new ImageViewUpdater(bitmap, imageToLoad);
							Activity activity = (Activity)imageToLoad.getImageView().getContext();
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

	private Bitmap fetchBitmap(ImageToLoadUrl imageToLoadUrl) {
		Bitmap bitmap = null;
		InputStream is = null;
		int retries = MAX_RETRIES;
		do {
			try {
				is = fetch(imageToLoadUrl);
				if(imageToLoadUrl.isCanCacheFile()) {
					putBitmapToCaches(is, imageToLoadUrl);
					// Log.v(TAG, "fetchBitmap - getting bitmap from cache");
					bitmap = memoryCache.get(imageToLoadUrl.toCacheKey());
					getBitmapFromCache(imageToLoadUrl);
				} else {
					bitmap = decodeBitmap(is);
				}
			} catch (Exception e) {
				Log.v(TAG, "fetchDrawable - Exception: " + imageToLoadUrl.toCacheKey().toString());
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
		ImageToLoadUrl imageToLoadUrl;

		public ImageViewUpdater(Bitmap bitmap, ImageToLoadUrl imageToLoadUrl) {
			this.bitmap = bitmap;
			this.imageToLoadUrl = imageToLoadUrl;
		}

		@Override
		public void run() {
			if(bitmap != null) {
				imageToLoadUrl.getImageView().setImageBitmap(bitmap);
				if(imageToLoadUrl.getOnImageLoadedListener() != null) {
					imageToLoadUrl.getOnImageLoadedListener().onImageLoaded(imageToLoadUrl.getUrl());
				}
			} else if(imageToLoadUrl.getOnImageLoadedListener() != null) {
				imageToLoadUrl.getOnImageLoadedListener().onLoadFailure(imageToLoadUrl.getUrl());
			}
		}
	}

	public void clearMemoryCache() {
		memoryCache.clear();
	}

	public void clearFileCache() {
		File dir = ExternalStorageHelper.getCacheDir(context);
		File files[] = dir.listFiles();
		for(File f : files) {
			f.delete();
		}
	}

}
