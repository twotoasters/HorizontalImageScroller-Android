package com.twotoasters.android.horizontalimagescroller.io;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;


import android.graphics.Bitmap;

public class MemoryCache {
	private ConcurrentHashMap<ImageUrlRequestCacheKey, SoftReference<Bitmap>> cache = new ConcurrentHashMap<ImageUrlRequestCacheKey, SoftReference<Bitmap>>();

	public boolean contains(ImageUrlRequestCacheKey key) {
		try {
			return get(key) != null;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public Bitmap get(ImageUrlRequestCacheKey key) {
		try {
			if(!cache.containsKey(key))
				return null;
			SoftReference<Bitmap> ref = cache.get(key);
			return ref.get();
		} catch (NullPointerException e) {
			return null;
		}
	}

	public void put(ImageUrlRequestCacheKey key, Bitmap bitmap) {
		try {
			cache.put(key, new SoftReference<Bitmap>(bitmap));
		} catch (NullPointerException e) {
		}
	}

	public void clear() {
		cache.clear();
	}

	public void putIfAbsent(ImageUrlRequestCacheKey key, Bitmap bitmap) {
		try {
			if(!contains(key)) {
				put(key, bitmap);
			}
		} catch (Exception e) {
		}
	}
}