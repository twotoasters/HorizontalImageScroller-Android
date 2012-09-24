package com.twotoasters.android.horizontalimagescroller.io;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import com.twotoasters.android.horizontalimagescroller.image.ImageToLoadUrlCacheKey;

import android.graphics.Bitmap;

public class MemoryCache {
	private ConcurrentHashMap<ImageToLoadUrlCacheKey, SoftReference<Bitmap>> cache = new ConcurrentHashMap<ImageToLoadUrlCacheKey, SoftReference<Bitmap>>();

	public boolean contains(ImageToLoadUrlCacheKey key) {
		try {
			return get(key) != null;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public Bitmap get(ImageToLoadUrlCacheKey key) {
		try {
			if(!cache.containsKey(key))
				return null;
			SoftReference<Bitmap> ref = cache.get(key);
			return ref.get();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public void put(ImageToLoadUrlCacheKey key, Bitmap bitmap) {
		try {
			cache.put(key, new SoftReference<Bitmap>(bitmap));
		} catch (NullPointerException e) {
		}
	}

	public void clear() {
		cache.clear();
	}

	public void putIfAbsent(ImageToLoadUrlCacheKey key, Bitmap bitmap) {
		try {
			if(!contains(key)) {
				put(key, bitmap);
			}
		} catch (Exception e) {
		}
	}
}